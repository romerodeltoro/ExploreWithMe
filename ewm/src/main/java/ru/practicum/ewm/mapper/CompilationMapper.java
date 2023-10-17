package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.model.compilation.Compilation;
import ru.practicum.ewm.model.compilation.CompilationDto;
import ru.practicum.ewm.model.compilation.NewCompilationDto;

@Mapper
public interface CompilationMapper {
    CompilationMapper INSTANCE = Mappers.getMapper(CompilationMapper.class);

    Compilation toCompilation(NewCompilationDto dto);
    CompilationDto toCompilationDto(Compilation compilation);
}
