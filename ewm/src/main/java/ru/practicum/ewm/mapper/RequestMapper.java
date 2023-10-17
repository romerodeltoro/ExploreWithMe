package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.model.request.ParticipationRequest;
import ru.practicum.ewm.model.request.ParticipationRequestDto;

@Mapper
public interface RequestMapper {

    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    ParticipationRequest toRequest(ParticipationRequestDto dto);
//    @Mapping(target = "event", source = "event.id")
//    @Mapping(target = "requester", source = "requester.id")
    ParticipationRequestDto toRequestDto(ParticipationRequest request);
}
