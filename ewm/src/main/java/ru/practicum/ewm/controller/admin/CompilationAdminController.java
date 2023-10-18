package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.compilation.CompilationDto;
import ru.practicum.ewm.model.compilation.NewCompilationDto;
import ru.practicum.ewm.model.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Validated
public class CompilationAdminController {

    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> createCompilation(@RequestBody @Valid NewCompilationDto compilationDto) {
        return ResponseEntity.status(201).body(compilationService.createCompilation(compilationDto));
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> createCompilation(
            @PathVariable Long compId,
            @RequestBody @Valid UpdateCompilationRequest updateCompilation) {
        return ResponseEntity.ok().body(compilationService.updateCompilation(compId, updateCompilation));
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Void> deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
        return ResponseEntity.status(204).build();
    }
}
