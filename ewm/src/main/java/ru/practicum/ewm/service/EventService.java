package ru.practicum.ewm.service;

import org.springframework.validation.annotation.Validated;
import ru.practicum.ewm.model.event.*;
import ru.practicum.ewm.model.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.model.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.model.request.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Validated
public interface EventService {
    EventFullDto createEvent(Long userId, @Valid EventDto eventDto);

    List<EventShortDto> getAllEvents(Long userId, Integer from, Integer size);

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

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest eventDto);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest eventDto);

    List<ParticipationRequestDto> getRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequest(
            Long userId, Long eventIds, EventRequestStatusUpdateRequest updateRequest1);

    EventFullDto getPublicEvent(Long id, HttpServletRequest request);

    List<EventShortDto> getPublicAllEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            String rangeStart,
            String rangeEnd,
            Boolean onlyAvailable,
            String sort,
            Integer from,
            Integer size);
}
