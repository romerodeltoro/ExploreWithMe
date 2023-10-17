package ru.practicum.ewm.service;

import ru.practicum.ewm.model.request.ParticipationRequestDto;

import java.util.List;

public interface RequestsService {
    ParticipationRequestDto createRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequests(Long userId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}
