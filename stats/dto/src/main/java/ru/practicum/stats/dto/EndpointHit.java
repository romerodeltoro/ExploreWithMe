package ru.practicum.stats.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EndpointHit {
    private Integer id;
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
