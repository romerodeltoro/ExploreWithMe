package ru.practicum.ewm.service;

import ru.practicum.ewm.model.compilation.CompilationDto;
import ru.practicum.ewm.model.compilation.NewCompilationDto;
import ru.practicum.ewm.model.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {


    CompilationDto createCompilation(NewCompilationDto compilationDto);

    CompilationDto updateCompilation(Long id, UpdateCompilationRequest updateCompilation);

    void deleteCompilation(Long compId);

    CompilationDto getCompilation(Long compId);

    List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size);
}
