package ru.practicum.ewm;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.client.StatsClientImpl;
import ru.practicum.stats.dto.EndpointHit;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EwnService {

    private final StatsClient client;

    public void saveStats(EndpointHit hit) {
        client.createEndpointHit(hit);
    }

    public ResponseEntity<Object> getStats(String start, String end, Boolean unique, List<String> uris) {
       return client.getViewStats(start, end, unique, uris);
    }
}
