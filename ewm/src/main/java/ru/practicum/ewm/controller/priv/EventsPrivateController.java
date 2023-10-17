package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.event.*;
import ru.practicum.ewm.model.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.model.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.model.request.ParticipationRequestDto;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users/{userId}/events")
public class EventsPrivateController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventFullDto> createEvent(
            @PathVariable Long userId,
            @RequestBody @Valid EventDto eventDto) {
        return ResponseEntity.status(201).body(eventService.createEvent(userId, eventDto));
    }

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getAllEvents(
            @PathVariable Long userId,
            @RequestParam(name = "from", defaultValue = "0", required = false) @Min(0) Integer from,
            @RequestParam(name = "size", defaultValue = "10", required = false) @Min(1) @Max(1000) Integer size) {
        return ResponseEntity.ok().body(eventService.getAllEvents(userId, from, size));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        return ResponseEntity.ok().body(eventService.getEvent(userId, eventId));
    }

    @PatchMapping("/{eventId}")
    public  ResponseEntity<EventFullDto> updateEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid UpdateEventUserRequest eventDto) {
        return ResponseEntity.ok().body(eventService.updateEventByUser(userId, eventId, eventDto));
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequests(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        return ResponseEntity.ok().body(eventService.getRequests(userId, eventId));
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequests(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        return ResponseEntity.ok().body(eventService.updateRequest(userId, eventId, updateRequest));
    }
}
