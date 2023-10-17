package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.request.ParticipationRequestDto;
import ru.practicum.ewm.service.RequestsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users/{userId}/requests")
public class RequestsPrivateController {

    private final RequestsService requestsService;

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> createRequest(
            @PathVariable Long userId,
            @RequestParam(name = "eventId") Long eventId) {
        return ResponseEntity.status(201).body(requestsService.createRequest(userId, eventId));
    }

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getRequests(@PathVariable Long userId) {
        return ResponseEntity.ok().body(requestsService.getRequests(userId));
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(
            @PathVariable Long userId,
            @PathVariable Long requestId) {
        return ResponseEntity.ok().body(requestsService.cancelRequest(userId, requestId));
    }
 }
