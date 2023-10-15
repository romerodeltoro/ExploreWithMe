package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.EventFullDto;
import ru.practicum.ewm.model.event.EventShort;
import ru.practicum.ewm.model.event.EventState;
import ru.practicum.ewm.service.EventService;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
public class EventsAdminController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getAllEvents(
            @RequestParam(name = "users", required = false) Set<Long> users,
            @RequestParam(name = "states", required = false) Set<EventState> states,
            @RequestParam(name = "categories", required = false) Set<Long> categories,
            @RequestParam(name = "rangeStart", required = false) String rangeStart,
            @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(name = "from", defaultValue = "0", required = false) @Min(0) Integer from,
            @RequestParam(name = "size", defaultValue = "10", required = false) @Min(1) @Max(1000) Integer size) {



        return ResponseEntity.ok().body(eventService.getToAdminAllEvents(users, states, categories, rangeStart, rangeEnd, from, size));
    }
}
