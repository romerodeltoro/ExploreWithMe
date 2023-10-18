package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.model.compilation.Compilation;
import ru.practicum.ewm.model.compilation.CompilationDto;
import ru.practicum.ewm.model.compilation.NewCompilationDto;

@Mapper
public interface CompilationMapper {
    CompilationMapper INSTANCE = Mappers.getMapper(CompilationMapper.class);


    Compilation newCompilationToCompilation(NewCompilationDto dto);
//    @Mapping(target = "events", source = "events", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "title", source = "title", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "pinned", source = "pinned", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    Compilation updateCompilationToCompilation(UpdateCompilationRequest compilationRequest);
    @Mapping(source = "events", target = "events", ignore = true)
    CompilationDto toCompilationDto(Compilation compilation);
}
