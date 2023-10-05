package ru.practicum.stats.server.service;

import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;

import java.util.List;

public interface HitService {
    EndpointHit createHit(EndpointHit hitDto);

    List<ViewStats> getStats(String start, String end, Boolean unique, List<String> uris);
}
