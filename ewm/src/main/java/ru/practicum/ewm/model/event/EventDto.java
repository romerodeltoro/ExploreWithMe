package ru.practicum.ewm.model.event;

import lombok.*;
import ru.practicum.ewm.model.location.LocationDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import ru.practicum.ewm.exception.Marker;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

    @NotBlank(groups = Marker.OnCreate.class)
    @Size(min = 3, max = 120)
    private String title;
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(min = 20, max = 2000)
    private String annotation;
    @NotBlank(groups = Marker.OnCreate.class)
    private Long category;
    private Boolean paid = false;
    @NotBlank(groups = Marker.OnCreate.class)
    private String eventDate;
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(min = 20, max = 7000)
    private String description;
    @NotBlank(groups = Marker.OnCreate.class)
    private LocationDto location;
    private Boolean requestModeration = true;
    private Integer participantLimit = 0;



}
