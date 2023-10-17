package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.event.EventFullDto;
import ru.practicum.ewm.model.event.EventShortDto;
import ru.practicum.ewm.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventPublicController {

    private final EventService eventService;

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable Long id, HttpServletRequest request) {
        return ResponseEntity.ok().body(eventService.getPublicEvent(id, request));
    }

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getAllEvents(
            @RequestParam(name = "text", required = false) String text,
            @RequestParam(name = "categories", required = false) List<Long> categories,
            @RequestParam(name = "paid", required = false) Boolean paid,
            @RequestParam(name = "rangeStart", required = false) String rangeStart,
            @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(name = "onlyAvailable", defaultValue = "false", required = false) Boolean onlyAvailable,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "from", defaultValue = "0", required = false) @Min(0) Integer from,
            @RequestParam(name = "size", defaultValue = "10", required = false) @Min(1) @Max(1000) Integer size) {

        return ResponseEntity.ok().body(eventService.getPublicAllEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size));
    }


}
