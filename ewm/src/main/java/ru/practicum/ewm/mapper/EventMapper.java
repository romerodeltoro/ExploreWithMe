package ru.practicum.ewm.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.*;

@Mapper
public interface EventMapper {

    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    Event dtoToEvent(EventDto eventDto);


    @Mapping(target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    Event eventUserUpdate(UpdateEventUserRequest newEvent, @MappingTarget Event event);


    @Mapping(target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    Event eventAdminUpdate(UpdateEventAdminRequest newEvent, @MappingTarget Event event);

    Category map(Long categoryId);

    @Mapping(target = "eventDate", expression = "java(DateTimeFormatter.ofPattern(\"yyyy-MM-dd HH:mm:ss\").format(event.getEventDate()))")
    @Mapping(target = "createdOn", expression = "java(DateTimeFormatter.ofPattern(\"yyyy-MM-dd HH:mm:ss\").format(event.getCreatedOn()))")
    @Mapping(target = "publishedOn", expression = "java(DateTimeFormatter.ofPattern(\"yyyy-MM-dd HH:mm:ss\").format(event.getCreatedOn()))")
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "eventDate", expression = "java(DateTimeFormatter.ofPattern(\"yyyy-MM-dd HH:mm:ss\").format(event.getEventDate()))")
    EventShortDto toEventShort(Event event);
}
