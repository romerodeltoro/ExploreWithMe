package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.EventDto;
import ru.practicum.ewm.model.event.EventShort;
import ru.practicum.ewm.service.EventService;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    @Override
    @Transactional
    public Event createEvent(Long userId, EventDto eventDto) {
        return null;
    }

    @Override
    public List<EventShort> getAllEvents(Long userId, Integer from, Integer size) {
        return null;
    }
}
