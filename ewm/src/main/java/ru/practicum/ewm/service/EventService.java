package ru.practicum.ewm.service;

import org.springframework.validation.annotation.Validated;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Validated
public interface EventService {
    EventFullDto createEvent(Long userId, @Valid EventDto eventDto);

    List<EventShort> getAllEvents(Long userId, Integer from, Integer size);

    EventFullDto getEvent(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, EventDto eventDto);

    List<EventFullDto> getToAdminAllEvents(
            Set<Long> users,
            Set<EventState> states,
            Set<Long> categories,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size);
}
