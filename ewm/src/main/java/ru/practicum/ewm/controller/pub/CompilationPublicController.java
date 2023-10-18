package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.compilation.CompilationDto;
import ru.practicum.ewm.service.CompilationService;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class CompilationPublicController {

    private final CompilationService compilationService;

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getCompilation(@PathVariable Long compId) {
        return ResponseEntity.ok().body(compilationService.getCompilation(compId));
    }

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getAllCompilations(
            @RequestParam(name = "pinned", required = false) Boolean pinned,
            @RequestParam(name = "from", defaultValue = "0", required = false) @Min(0) Integer from,
            @RequestParam(name = "size", defaultValue = "10", required = false) @Min(1) @Max(1000) Integer size) {
        return ResponseEntity.ok().body(compilationService.getAllCompilations(pinned, from, size));
    }
}
