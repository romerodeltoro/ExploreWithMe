package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.EventDto;
import ru.practicum.ewm.model.event.EventShort;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users/{userId}/events")
public class EventsPrivateController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<Event> createEvent(
            @PathVariable Long userId,
            @RequestBody @Valid EventDto eventDto) {
        return ResponseEntity.status(201).body(eventService.createEvent(userId, eventDto));
    }

    @GetMapping
    public ResponseEntity<List<EventShort>> getEvents(
            @PathVariable Long userId,
            @RequestParam Integer from,
            @RequestParam Integer size) {
        return ResponseEntity.ok().body(eventService.getAllEvents(userId, from, size));
    }


}
