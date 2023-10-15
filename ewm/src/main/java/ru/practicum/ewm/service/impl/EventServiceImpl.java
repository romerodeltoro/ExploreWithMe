package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.ewm.exception.CategoriesNotFoundException;
import ru.practicum.ewm.exception.EventDateValidateException;
import ru.practicum.ewm.exception.EventNotFoundException;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.LocationMapper;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.*;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.storage.CategoryRepository;
import ru.practicum.ewm.storage.EventRepository;
import ru.practicum.ewm.storage.LocationRepository;
import ru.practicum.ewm.storage.UserRepository;

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
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, EventDto eventDto) {

        User user = getUserByIdOrElseThrow(userId);
        Category category = getCategoryByIdOrElseThrow(eventDto.getCategory());
        eventDateValidated(eventDto.getEventDate());
        Location location = locationRepository.save(LocationMapper.INSTANCE.toLocation(eventDto.getLocation()));
        Event event = eventRepository.save(EventMapper.INSTANCE.toEvent(eventDto));
        event.setInitiator(user);
        event.setLocation(location);
        event.setCategory(category);
        log.info("Создано новое событие - {}", event.getTitle());

        return EventMapper.INSTANCE.toEventFullDto(event);
    }

    @Override
    public List<EventShort> getAllEvents(Long userId, Integer from, Integer size) {
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
        User user = getUserByIdOrElseThrow(userId);
        Event event = eventRepository.findByIdAndInitiatorIdAndStateIn(eventId, userId, List.of(EventState.PENDING, EventState.CANCELED));
        Event updatedEvent = updatingEventWithDto(event, eventDto);
        eventRepository.flush();

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

        return events.stream().map(EventMapper.INSTANCE::toEventFullDto).collect(Collectors.toList());
    }

    private Event getEventByIdOrElseThrow(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(
                String.format("Event with id=%d was not found", id)));

    }

    private User getUserByIdOrElseThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(
                String.format("User with id=%d was not found", id)));
    }

    private Category getCategoryByIdOrElseThrow(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new CategoriesNotFoundException(
                String.format("Category with id=%d was not found", id)));
    }

    private void eventDateValidated(String stringDate) {
        LocalDateTime date = LocalDateTime.parse(stringDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if(date.minusHours(2).isBefore(LocalDateTime.now())) {
            throw new EventDateValidateException(
                    String.format("Field: eventDate. " +
                            "Error: должно содержать дату, которая еще не наступила. Value: %S", stringDate));

        }
    }

    private Event updatingEventWithDto(Event event, EventDto dto) {
        event.setTitle(dto.getTitle() != null ? dto.getTitle() : event.getTitle());
        event.setAnnotation(dto.getAnnotation() != null ? dto.getAnnotation() : event.getAnnotation());
        event.setDescription(dto.getDescription() != null ? dto.getDescription() : event.getDescription());
        event.setParticipantLimit(
                dto.getParticipantLimit() != 0 ? dto.getParticipantLimit() : event.getParticipantLimit());
//        event.setRequestModeration(dto.getRequestModeration());
//        event.setPaid(dto.getPaid());
        event.setState(EventState.CANCELED);

        return event;
    }

}
