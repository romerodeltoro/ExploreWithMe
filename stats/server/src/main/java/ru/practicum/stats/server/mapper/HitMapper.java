package ru.practicum.stats.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.server.model.Hit;

import java.util.List;

@Mapper
public interface HitMapper {

    HitMapper INSTANCE = Mappers.getMapper(HitMapper.class);

    @Mapping(source = "timestamp", target = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    EndpointHit toHitDto(Hit hit);

    @Mapping(source = "timestamp", target = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    Hit toHit(EndpointHit hitDto);


    ViewStats toViewStats(Hit hit);

}
