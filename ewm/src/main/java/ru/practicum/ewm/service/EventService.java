package ru.practicum.ewm.service;

import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.EventDto;
import ru.practicum.ewm.model.event.EventShort;

import java.util.List;

public interface EventService {
    Event createEvent(Long userId, EventDto eventDto);

    List<EventShort> getAllEvents(Long userId, Integer from, Integer size);
}
