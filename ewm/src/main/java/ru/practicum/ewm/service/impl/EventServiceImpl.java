package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
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
