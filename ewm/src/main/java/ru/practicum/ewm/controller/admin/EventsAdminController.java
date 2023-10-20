package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.event.EventFullDto;
import ru.practicum.ewm.model.event.UpdateEventAdminRequest;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
public class EventsAdminController {

    private final EventService eventService;

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(
            @PathVariable Long eventId,
            @RequestBody @Valid UpdateEventAdminRequest eventDto) {
        return ResponseEntity.ok().body(eventService.updateEventByAdmin(eventId, eventDto));
    }

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getAllEvents(
            @RequestParam Map<String, String> queryParams,
            @RequestParam(name = "from", defaultValue = "0", required = false) @Min(0) Integer from,
            @RequestParam(name = "size", defaultValue = "10", required = false) @Min(1) @Max(1000) Integer size) {

        return ResponseEntity.ok().body(eventService.getAdminAllEvents(queryParams, from, size));
    }

}
