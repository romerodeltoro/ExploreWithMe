package ru.practicum.stats.dto;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class ViewStats {

    private String app;
    private String uri;
    private Long hits;
}
