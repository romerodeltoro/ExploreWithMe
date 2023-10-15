package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.EventDto;
import ru.practicum.ewm.model.event.EventFullDto;
import ru.practicum.ewm.model.event.EventShort;

@Mapper
public interface EventMapper {

    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    Event toEvent(EventDto eventDto);

    Category map(Long categoryId);

    @Mapping(target = "eventDate", expression = "java(DateTimeFormatter.ofPattern(\"yyyy-MM-dd HH:mm:ss\").format(event.getEventDate()))")
    @Mapping(target = "createdOn", expression = "java(DateTimeFormatter.ofPattern(\"yyyy-MM-dd HH:mm:ss\").format(event.getCreatedOn()))")
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "eventDate", expression = "java(DateTimeFormatter.ofPattern(\"yyyy-MM-dd HH:mm:ss\").format(event.getEventDate()))")
    EventShort toEventShort(Event event);
}
