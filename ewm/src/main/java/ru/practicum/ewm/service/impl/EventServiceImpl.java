package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.LocationMapper;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.location.Location;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.*;
import ru.practicum.ewm.model.request.*;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.storage.*;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.EndpointHit;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final StatsClient client;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, EventDto eventDto) {

        User user = getUserByIdOrElseThrow(userId);
        Category category = getCategoryByIdOrElseThrow(eventDto.getCategory());
        eventDateValidated(eventDto.getEventDate());
        Location location = locationRepository.save(LocationMapper.INSTANCE.toLocation(eventDto.getLocation()));
        Event event = eventRepository.save(EventMapper.INSTANCE.dtoToEvent(eventDto));
        event.setInitiator(user);
        event.setLocation(location);
        event.setCategory(category);
        log.info("Создано новое событие - {}", event.getTitle());

        return EventMapper.INSTANCE.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getAllEvents(Long userId, Integer from, Integer size) {
        User user = getUserByIdOrElseThrow(userId);
        Pageable pageable = PageRequest.of(from, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable).toList();
        log.info("Получен список событий добавленных пользователем - {}", user);

        return events.stream()
                .map(EventMapper.INSTANCE::toEventShort)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        User user = getUserByIdOrElseThrow(userId);
        Event event = getEventByIdOrElseThrow(eventId);
        log.info("Получена полная информация о событии - {}, добавленных пользователем - {}", event.getTitle(), user);

        return EventMapper.INSTANCE.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, EventDto eventDto) {
        return null;
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest eventDto) {
        User user = getUserByIdOrElseThrow(userId);
        Event event = eventRepository
                .findByIdAndInitiatorIdAndStateIn(eventId, userId, List.of(EventState.PENDING, EventState.CANCELED));
        Event updatedEvent = updatingEventByUserWithDto(event, eventDto);
        log.info("Событие с id={} изменено пользователем с id={}", eventId, user.getId());

        return EventMapper.INSTANCE.toEventFullDto(updatedEvent);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest eventDto) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PENDING)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
        Event updatedEvent = updatingEventByAdminWithDto(event, eventDto);
        log.info("Событие с id={} изменено админом", eventId);

        return EventMapper.INSTANCE.toEventFullDto(updatedEvent);
    }


    @Override
    public List<EventFullDto> getToAdminAllEvents(
            Set<Long> users,
            Set<EventState> states,
            Set<Long> categories,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size) {

        Pageable pageable = PageRequest.of(from, size);
        LocalDateTime start = LocalDateTime.parse(rangeStart, formatter);
        LocalDateTime end = LocalDateTime.parse(rangeEnd, formatter);

        List<Event> events = eventRepository
                .findAllByInitiatorIdAndStateAndCategoryAndEventDate(
                        users, states, categories, start, end, pageable)
                .toList();
        log.info("Админом получен список событий");

        return events.stream().map(EventMapper.INSTANCE::toEventFullDto).collect(Collectors.toList());
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        getUserByIdOrElseThrow(userId);
        getEventByIdOrElseThrow(eventId);
        List<ParticipationRequest> requests = requestRepository.findAllByEvent(eventId);
        log.info("Пользователь с id={} получил список запросов на участие в событии с id={}", userId, eventId);

        return requests.stream().map(RequestMapper.INSTANCE::toRequestDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequest(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest updateRequest) {

        if (updateRequest.getStatus().equals(RequestStatus.PENDING) ||
                updateRequest.getStatus().equals(RequestStatus.CANCELED)) {
            throw new BadRequestException(String.format("Request must have not status %S", updateRequest.getStatus()));
        }

        Event event = getEventByIdOrElseThrow(eventId);
        List<ParticipationRequest> requests = requestRepository.findAllByIds(updateRequest.getRequestIds());

        if (updateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
            for (ParticipationRequest request : requests) {
                if (event.getConfirmedRequests() < event.getParticipantLimit()) {
                    request.setStatus(updateRequest.getStatus());
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                    event.setAvailable(false);
                }
            }
            if (event.getConfirmedRequests().equals(event.getParticipantLimit())) {
                throw new OperationConditionsException("The participant limit has been reached");
            }
        } else {
            requests.stream().forEach(r -> r.setStatus(RequestStatus.REJECTED));
        }

        EventRequestStatusUpdateResult updateResult = new EventRequestStatusUpdateResult();
        List<ParticipationRequest> confirmedRequests = requestRepository.findAllByStatus(RequestStatus.CONFIRMED);
        List<ParticipationRequest> rejectedRequests = requestRepository.findAllByStatus(RequestStatus.REJECTED);
        updateResult.setConfirmedRequests(confirmedRequests.stream()
                .map(RequestMapper.INSTANCE::toRequestDto).collect(Collectors.toList()));
        updateResult.setRejectedRequests(rejectedRequests
                .stream().map(RequestMapper.INSTANCE::toRequestDto).collect(Collectors.toList()));

        return updateResult;
    }

    @Override
    @Transactional
    public EventFullDto getPublicEvent(Long id, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", id)));
        createHit(request);
        event.setViews(event.getViews() + 1);
        log.info("Просмотрена полная информация о событии с id={}", id);

        return EventMapper.INSTANCE.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getPublicAllEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            String rangeStart,
            String rangeEnd,
            Boolean onlyAvailable,
            String sort,
            Integer from,
            Integer size) {

        Pageable pageable = PageRequest.of(from, size);
        LocalDateTime start = LocalDateTime.parse(rangeStart, formatter);
        LocalDateTime end = LocalDateTime.parse(rangeEnd, formatter);

        return null;
    }


    @Override
    public List<EventShortDto> getAllEventsBySearch(Map<String, String> queryParams, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        SearchFilter filter = queryParamsToSearchFilter(queryParams);
        List<Specification<Event>> specifications = searchFilterToSpecifications(filter);
        List<Event> events = eventRepository.findAll(specifications.stream()
                .reduce(Specification::and)
                .orElseThrow(() -> new RuntimeException("Не заданы условия поиска")), pageable).toList();

        return events.stream().map(EventMapper.INSTANCE::toEventShort).collect(Collectors.toList());

    }

    private List<Specification<Event>> searchFilterToSpecifications(SearchFilter filter) {
        List<Specification<Event>> specifications = new ArrayList<>();
       specifications.add(filter.getText() != null ? searchByText(filter.getText()) : null);
        specifications.add(filter.getCategories() != null ? searchByCategories(filter.getCategories()) : null);
        specifications.add(filter.getPaid() != null ? searchByPaid(filter.getPaid()) : null);
        specifications.add(filter.getRangeStart() != null ?
                searchByRange(filter.getRangeStart(), filter.getRangeEnd()) : searchByNullRange());
          specifications.add(filter.getOnlyAvailable().equals(true) ? searchByAvailable(filter.getOnlyAvailable()) : null);
        specifications.add(searchByState(filter.getState()));
        specifications.add(searchSort(filter.getSort()));

        return specifications.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Specification<Event> searchByText(String text) {
        return (root, query, cb) -> cb
                .or(cb.like(cb.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("description")), "%" + text.toLowerCase() + "%"));
    }

    private Specification<Event> searchByCategories(List<Long> categories) {
        return (root, query, cb) -> cb.in(root.get("category").get("id")).value(categories);
    }

    private Specification<Event> searchByPaid(Boolean paid) {
        return (root, query, cb) -> cb.equal(root.get("paid"), paid);
    }

    private Specification<Event> searchByRange(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> cb.between(root.get("eventDate"), start, end);
    }

    private Specification<Event> searchByNullRange() {
        return (root, query, cb) -> cb.greaterThan(root.get("eventDate"), LocalDateTime.now());
    }

    private Specification<Event> searchByAvailable(Boolean available) {
        return (root, query, cb) -> cb.equal(root.get("available"), available);
    }

    private Specification<Event> searchByState(EventState state) {
        return (root, query, cb) -> cb.equal(root.get("state"), state);
    }

    private Specification<Event> searchSort(String sort) {
        return (root, query, cb) -> {
            if (sort.equalsIgnoreCase("EVENT_DATE")) {
                return query.orderBy(cb.asc(root.get("eventDate"))).getRestriction();
            } else {
                return query.orderBy(cb.desc(root.get("views"))).getRestriction();
            }
        };
    }



    private SearchFilter queryParamsToSearchFilter(Map<String, String> queryParams) {

        return SearchFilter.builder()
                .text(queryParams.get("text"))
                .categories(Arrays.stream(queryParams.get("categories").split(","))
                        .map(s -> Long.parseLong(s.trim())).collect(Collectors.toList()))
                .paid(Boolean.parseBoolean(queryParams.get("paid")))
                .rangeStart(LocalDateTime.parse(queryParams.get("rangeStart"), formatter))
                .rangeEnd(LocalDateTime.parse(queryParams.get("rangeEnd"), formatter))
                .onlyAvailable(Boolean.parseBoolean(
                        queryParams.get("onlyAvailable") != null ? queryParams.get("onlyAvailable") : "false"))
                .sort(queryParams.get("sort"))
                .state(EventState.PUBLISHED)
                .build();
    }

    private Event getEventByIdOrElseThrow(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("Event with id=%d was not found", id)));

    }

    private User getUserByIdOrElseThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("User with id=%d was not found", id)));
    }

    private Category getCategoryByIdOrElseThrow(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("Category with id=%d was not found", id)));
    }

    private void eventDateValidated(String stringDate) {
        LocalDateTime date = LocalDateTime.parse(stringDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if(date.minusHours(2).isBefore(LocalDateTime.now())) {
            throw new EventValidateException(
                    String.format("Field: eventDate. " +
                            "Error: должно содержать дату, которая еще не наступила. Value: %S", stringDate));
        }
    }

    private Event updatingEventByUserWithDto(Event event, UpdateEventUserRequest dto) {

        if(dto.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
            event.setState(EventState.CANCELED);
        }

        event.setTitle(dto.getTitle() != null ? dto.getTitle() : event.getTitle());
        event.setAnnotation(dto.getAnnotation() != null ? dto.getAnnotation() : event.getAnnotation());
        event.setDescription(dto.getDescription() != null ? dto.getDescription() : event.getDescription());
        event.setParticipantLimit(
                dto.getParticipantLimit() != null ? dto.getParticipantLimit() : event.getParticipantLimit());
        event.setEventDate(dto.getEventDate() != null ?
                LocalDateTime.parse(dto.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : event.getEventDate());
        event.setLocation(dto.getLocation() != null ?
                locationRepository.save(LocationMapper.INSTANCE.toLocation(dto.getLocation()))
                : event.getLocation());
        event.setPaid(dto.getTitle() != null ? dto.getPaid() : event.getPaid());
        event.setRequestModeration(dto.getRequestModeration() != null ? dto.getRequestModeration()
                : event.getRequestModeration());

        return event;
    }

    private Event updatingEventByAdminWithDto(Event event, UpdateEventAdminRequest dto) {

        if (dto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
            if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new EventValidateException("Cannot publish the event because the date of the event " +
                        "must be no earlier than an hour from the date of publication");
            }
            if (!event.getState().equals(EventState.PENDING)) {
                throw new EventValidateException("Cannot publish the event because " +
                        "it's not in the right state: PENDING");
            }
            event.setState(EventState.PUBLISHED);
            event.setStateAction(StateAction.PUBLISH_EVENT);
            event.setTitle(dto.getTitle() != null ? dto.getTitle() : event.getTitle());
            event.setAnnotation(dto.getAnnotation() != null ? dto.getAnnotation() : event.getAnnotation());
            event.setDescription(dto.getDescription() != null ? dto.getDescription() : event.getDescription());
            event.setParticipantLimit(
                    dto.getParticipantLimit() != null ? dto.getParticipantLimit() : event.getParticipantLimit());
            event.setEventDate(dto.getEventDate() != null ?
                    LocalDateTime.parse(dto.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    : event.getEventDate());
            event.setLocation(dto.getLocation() != null ?
                    locationRepository.save(LocationMapper.INSTANCE.toLocation(dto.getLocation()))
                    : event.getLocation());
            event.setPaid(dto.getTitle() != null ? dto.getPaid() : event.getPaid());
            event.setRequestModeration(dto.getRequestModeration() != null ? dto.getRequestModeration()
                    : event.getRequestModeration());
            event.setPublishedOn(LocalDateTime.now());
        }
        if (dto.getStateAction().equals(StateAction.REJECT_EVENT)) {
            if (!event.getState().equals(EventState.PUBLISHED)) {
                throw new EventValidateException("Cannot publish the event because " +
                        "it's not in the right state: PUBLISHED");
            }
            event.setState(EventState.CANCELED);
            event.setStateAction(StateAction.REJECT_EVENT);
        }

        return event;
    }

    private void createHit(HttpServletRequest request) {
        EndpointHit hit = new EndpointHit();
        hit.setUri(request.getRequestURI());
        hit.setIp(request.getRemoteAddr());
        hit.setApp("explore-with-me");

        client.createEndpointHit(hit);
    }

}
