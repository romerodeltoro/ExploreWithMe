package ru.practicum.ewm;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.EndpointHit;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class EwnController {

    private final EwnService service;

    @PostMapping
    public ResponseEntity<Void> saveStats(@RequestBody EndpointHit hit) {
        service.saveStats(hit);
        return ResponseEntity.status(200).build();
    }

    @GetMapping
    public ResponseEntity<Object> getStats(
            @RequestParam(name = "start") String start,
            @RequestParam(name = "end") String end,
            @RequestParam(name = "unique", defaultValue = "false", required = false) Boolean unique,
            @RequestParam(name = "uris", required = false) List<String> uris
    ) {
        return ResponseEntity.ok().body(service.getStats(start, end, unique, uris));
    }
}
