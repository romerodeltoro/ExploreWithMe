package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.LocationMapper;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.comment.CommentDto;
import ru.practicum.ewm.model.event.*;
import ru.practicum.ewm.model.location.Location;
import ru.practicum.ewm.model.request.*;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.SearchSpecificationBuilder;
import ru.practicum.ewm.storage.*;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;
    private final StatsClient client;
    private final SearchSpecificationBuilder specificationBuilder;


    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, EventDto eventDto) {

        User user = getUserByIdOrElseThrow(userId);
        Category category = getCategoryByIdOrElseThrow(eventDto.getCategory());
        eventUpdateDateValidated(eventDto.getEventDate());
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
        List<EventShortDto> eventShortDtos = events.stream()
                .map(e -> {
                    EventShortDto event = EventMapper.INSTANCE.toEventShort(e);
                    event.setComments(commentRepository.findAllByEventId(e.getId()).size());
                    return event;
                })
                .collect(Collectors.toList());

        log.info("Получен список событий добавленных пользователем - {}", user);
        return eventShortDtos;
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        User user = getUserByIdOrElseThrow(userId);

        Event event = getEventByIdOrElseThrow(eventId);
        EventFullDto eventDto = EventMapper.INSTANCE.toEventFullDto(event);
        eventDto.setComments(getCommentsByEventId(eventId));

        log.info("Получена полная информация о событии - {}, добавленных пользователем - {}", event.getTitle(), user);
        return eventDto;
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest eventDto) {

        if (eventDto.getEventDate() != null) {
            eventUpdateDateValidated(eventDto.getEventDate());
        }
        User user = getUserByIdOrElseThrow(userId);
        Event event = eventRepository
                .findByIdAndInitiatorIdAndStateIn(eventId, userId, List.of(EventState.PENDING, EventState.CANCELED));
        if (event != null) {
            Event updatedEvent = updatingEventByUserWithDto(event, eventDto);

            log.info("Событие с id={} изменено пользователем с id={}", eventId, user.getId());
            return EventMapper.INSTANCE.toEventFullDto(updatedEvent);
        } else {
            throw new ConflictException(String.format("Event with id=%d has been published", eventId));
        }
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest eventDto) {

        if (eventDto.getEventDate() != null) {
            eventUpdateDateValidated(eventDto.getEventDate());
        }
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (event.getState().equals(EventState.PENDING)) {
            Event updatedEvent = updatingEventByAdminWithDto(event, eventDto);

            log.info("Событие с id={} изменено админом", eventId);
            return EventMapper.INSTANCE.toEventFullDto(updatedEvent);
        } else {
            throw new ConflictException(String.format("Event with id=%d has been published", eventId));
        }
    }

    @Override
    @Transactional
    public List<EventFullDto> getAdminAllEvents(Map<String, String> queryParams, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        SearchFilter filter = SearchFilter.queryParamsToSearchFilter(queryParams);
        List<Specification<Event>> specifications = specificationBuilder.searchFilterToAdminSpecifications(filter);
        List<Event> events = eventRepository.findAll(specifications.stream()
                .reduce(Specification::and)
                .orElseThrow(() -> new BadRequestException("Incorrectly made request")), pageable).toList();
        List<EventFullDto> eventFullDtos = events.stream()
                .map(e -> {
                    EventFullDto event = EventMapper.INSTANCE.toEventFullDto(e);
                    event.setComments(getCommentsByEventId(e.getId()));
                    return event;
                })
                .collect(Collectors.toList());

        log.info("Админом получен список событий по заданным параметрам о событии");
        return eventFullDtos;
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

        if (requests.isEmpty()) {
            throw new ConflictException("The status cannot be changed because the " +
                    "request being modified has an incorrect status");
        }

        if (updateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
            if (event.getConfirmedRequests().equals(event.getParticipantLimit())) {
                throw new OperationConditionsException("The participant limit has been reached");
            }
            for (ParticipationRequest request : requests) {
                if (event.getConfirmedRequests() < event.getParticipantLimit()) {
                    request.setStatus(updateRequest.getStatus());
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                    event.setAvailable(false);
                }
            }
        } else {
            requests.stream().forEach(r -> r.setStatus(RequestStatus.REJECTED));
        }

        EventRequestStatusUpdateResult updateResult = new EventRequestStatusUpdateResult();
        List<ParticipationRequest> confirmedRequests = requestRepository.findAllByStatusOrderByIdDesc(RequestStatus.CONFIRMED);
        List<ParticipationRequest> rejectedRequests = requestRepository.findAllByStatusOrderByIdDesc(RequestStatus.REJECTED);
        updateResult.setConfirmedRequests(confirmedRequests.stream()
                .map(RequestMapper.INSTANCE::toRequestDto).collect(Collectors.toList()));
        updateResult.setRejectedRequests(rejectedRequests
                .stream().map(RequestMapper.INSTANCE::toRequestDto).collect(Collectors.toList()));

        log.info("Заявка на участие в событии с id={} изменена", eventId);
        return updateResult;
    }

    @Override
    @Transactional
    @SneakyThrows
    public EventFullDto getPublicEvent(Long id, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", id)));

        List<ViewStats> stats = getViewStats(request);
        createHit(request);
        List<ViewStats> newStats = getViewStats(request);
        if (!stats.equals(newStats)) {
            event.setViews(event.getViews() + 1);
        }
        EventFullDto eventDto = EventMapper.INSTANCE.toEventFullDto(event);
        eventDto.setComments(getCommentsByEventId(id));

        log.info("Просмотрена полная информация о событии с id={}", id);
        return eventDto;
    }

    @SneakyThrows
    private List<ViewStats> getViewStats(HttpServletRequest request) {

        String rangeStart = URLEncoder.encode(
                LocalDateTime.now().minusYears(10).withNano(0).toString(),
                StandardCharsets.UTF_8).replace("T", "%20");
        String rangeEnd = URLEncoder.encode(
                LocalDateTime.now().plusYears(25).withNano(0).toString(),
                StandardCharsets.UTF_8).replace("T", "%20");

        List<String> eventPaths = Collections.singletonList(request.getRequestURI());

        return (List<ViewStats>) client.getViewStats(rangeStart, rangeEnd, true, eventPaths).getBody();
    }


    @Override
    public List<EventShortDto> getPublicAllEvents(
            Map<String, String> queryParams, Integer from, Integer size, HttpServletRequest request) {

        Pageable pageable = PageRequest.of(from, size);
        SearchFilter filter = SearchFilter.queryParamsToSearchFilter(queryParams);
        filter.setState(EventState.PUBLISHED);
        List<Specification<Event>> specifications = specificationBuilder.searchFilterToSpecifications(filter);
        List<Event> events = eventRepository.findAll(specifications.stream()
                .reduce(Specification::and)
                .orElseThrow(() -> new BadRequestException("Incorrectly made request")), pageable).toList();
        List<EventShortDto> eventShortDtos = events.stream()
                .map(e -> {
                    EventShortDto event = EventMapper.INSTANCE.toEventShort(e);
                    event.setComments(commentRepository.findAllByEventId(e.getId()).size());
                    return event;
                })
                .collect(Collectors.toList());

        createHit(request);

        log.info("Получен публичный список событий по заданным параметрам о событии");
        return eventShortDtos;

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

        if (date.minusHours(2).isBefore(LocalDateTime.now())) {
            throw new EventValidateException(
                    String.format("EventDate " +
                            " должно содержать дату, которая еще не наступила. Value: %S", stringDate));
        }
    }

    private void eventUpdateDateValidated(String stringDate) {
        LocalDateTime date = LocalDateTime.parse(stringDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (date.isBefore(LocalDateTime.now())) {
            throw new BadRequestException(
                    String.format("EventDate" +
                            " должно содержать дату, которая еще не наступила. Value: %S", stringDate));
        }
    }

    private Event updatingEventByUserWithDto(Event event, UpdateEventUserRequest dto) {

        if (dto.getStateAction() == null) {
            EventMapper.INSTANCE.eventUserUpdate(dto, event);

        } else if (dto.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
            event.setState(EventState.CANCELED);
        } else if (dto.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
            event.setState(EventState.PENDING);
        }

        return event;
    }

    private Event updatingEventByAdminWithDto(Event event, UpdateEventAdminRequest dto) {

        if (dto.getStateAction() == null) {

            EventMapper.INSTANCE.eventAdminUpdate(dto, event);
            if (dto.getCategory() != null) {
                event.setCategory(getCategoryByIdOrElseThrow(dto.getCategory()));
            }

        } else if (dto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {

            if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ConflictException("Cannot publish the event because the date of the event " +
                        "must be no earlier than an hour from the date of publication");
            }
            if (!event.getState().equals(EventState.PENDING)) {
                throw new ConflictException("Cannot publish the event because " +
                        "it's not in the right state: PENDING");
            }
            EventMapper.INSTANCE.eventAdminUpdate(dto, event);
            if (dto.getCategory() != null) {
                event.setCategory(getCategoryByIdOrElseThrow(dto.getCategory()));
            }
            event.setState(EventState.PUBLISHED);
            event.setStateAction(StateAction.PUBLISH_EVENT);
            event.setPublishedOn(LocalDateTime.now());

        } else if (dto.getStateAction().equals(StateAction.REJECT_EVENT)) {

            if (event.getState().equals(EventState.PUBLISHED)) {
                throw new ConflictException(String.format("Cannot publish the event with id=%d ", event.getId()));
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

    private List<CommentDto> getCommentsByEventId(Long id) {
        return commentRepository.findAllByEventId(id)
                .stream()
                .map(CommentMapper.INSTANCE::toCommentDto)
                .collect(Collectors.toList());
    }

}
