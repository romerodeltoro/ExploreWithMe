package ru.practicum.stats.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stats.dto.EndpointHit;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsClient {

    private String statsUrl = "http://localhost:9090";

    private final RestTemplate restTemplate;

    public HttpStatus createEndpointHit(EndpointHit hit) {
        ResponseEntity<EndpointHit> response = restTemplate.postForEntity(statsUrl + "/hit", hit, EndpointHit.class);
        return response.getStatusCode();
    }

    public Object getViewStats(String start, String end, Boolean unique, List<String> uris) {
//        start = URLDecoder.decode(start, StandardCharsets.UTF_8.toString());

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("start", start);
        queryParams.put("end", end);
        queryParams.put("unique", unique);
        queryParams.put("uris", uris);

        ResponseEntity<Object> response = restTemplate
                .getForEntity(statsUrl + "/stats?start={start}&end={end}&uris={uris}&unique={unique}"
                        , Object.class, queryParams);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Что-то пошло не так: " + response.getStatusCode());
        }
    }
}
