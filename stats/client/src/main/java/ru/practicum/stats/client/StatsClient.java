package ru.practicum.stats.client;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.stats.dto.EndpointHit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient{

    private static final String ENDPOINTHIT_API_PREFIX = "/hit";
    private static final String VIEWSTATS_API_PREFIX = "/stats";

    @Autowired
    public StatsClient(@Value("${stats-client.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public void createEndpointHit(EndpointHit hit) {
        post(ENDPOINTHIT_API_PREFIX, hit);
    }

    public ResponseEntity<Object> getViewStats(String start, String end, Boolean unique, List<String> uris) {

        Map<String, Object> parameters;

        if (uris == null) {
            parameters = Map.of( "start", start,"end", end,"unique", unique);
            return get(VIEWSTATS_API_PREFIX + "?start={start}&end={end}&unique={unique}", parameters);
        } else {
            parameters = Map.of("start", start,"end", end,"uris", uris,"unique", unique);
            return get(VIEWSTATS_API_PREFIX + "?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
        }


    }
}
