package ru.practicum.ewm.model.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.exception.BadRequestException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Getter
@Setter
@Builder
public class SearchFilter {
    private String text;
    private List<Long> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private String sort;
    private EventState state;
    private List<Long> users;
    private List<EventState> states;


    public static SearchFilter queryParamsToSearchFilter(Map<String, String> queryParams) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        SearchFilter filter = SearchFilter.builder()
                .text(queryParams.getOrDefault("text", null))
                .categories(queryParams.containsKey("categories") ?
                        Arrays.stream(queryParams.get("categories").split(","))
                                .map(s -> Long.parseLong(s.trim())).collect(Collectors.toList()) : null)
                .paid(queryParams.containsKey("paid") ? Boolean.parseBoolean(queryParams.get("paid")) : null)
                .rangeStart(queryParams.containsKey("rangeStart") ? LocalDateTime.parse(queryParams.get("rangeStart"), formatter) : null)
                .rangeEnd(queryParams.containsKey("rangeEnd") ? LocalDateTime.parse(queryParams.get("rangeEnd"), formatter) : null)
                .onlyAvailable(queryParams.containsKey("onlyAvailable") && Boolean.parseBoolean(
                        queryParams.get("onlyAvailable")))
                .sort(queryParams.getOrDefault("sort", "EVENT_DATE"))
                .users(queryParams.containsKey("users") ? Arrays.stream(queryParams.get("users").split(","))
                        .map(u -> Long.parseLong(u.trim())).collect(Collectors.toList()) : null)
                .states(queryParams.containsKey("states") ? Arrays.stream(queryParams.get("states")
                        .split(",")).map(EventState::valueOf).collect(Collectors.toList()) : null)
                .build();

        if (filter.getRangeEnd() != null && filter.getRangeStart() != null) {
            if (filter.getRangeEnd().isBefore(filter.getRangeStart())) {
                throw new BadRequestException("RangeEnd must be after rangeStart");
            }
        }

        return filter;
    }

}




