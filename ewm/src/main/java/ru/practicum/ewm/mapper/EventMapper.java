package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.*;

@Mapper
public interface EventMapper {

    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    Event dtoToEvent(EventDto eventDto);

    @Mapping(target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    Event userRequestToEvent(UpdateEventUserRequest newEvent);
    @Mapping(target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    Event adminRequestToEvent(UpdateEventAdminRequest newEvent);

    Category map(Long categoryId);

    @Mapping(target = "eventDate", expression = "java(DateTimeFormatter.ofPattern(\"yyyy-MM-dd HH:mm:ss\").format(event.getEventDate()))")
    @Mapping(target = "createdOn", expression = "java(DateTimeFormatter.ofPattern(\"yyyy-MM-dd HH:mm:ss\").format(event.getCreatedOn()))")
    @Mapping(target = "publishedOn", expression = "java(DateTimeFormatter.ofPattern(\"yyyy-MM-dd HH:mm:ss\").format(event.getCreatedOn()))")
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "eventDate", expression = "java(DateTimeFormatter.ofPattern(\"yyyy-MM-dd HH:mm:ss\").format(event.getEventDate()))")
    EventShortDto toEventShort(Event event);
}
