package ru.practicum.ewm.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.exception.Marker;
import ru.practicum.ewm.model.location.LocationDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

    @NotBlank(groups = Marker.OnCreate.class)
    @Size(min = 3, max = 120)
    private String title;
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;
    @NotNull(groups = Marker.OnCreate.class)
    private Long category;
    private Boolean paid = false;
    @NotBlank(groups = Marker.OnCreate.class)
    private String eventDate;
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    @NotBlank(groups = Marker.OnCreate.class)
    private LocationDto location;
    private Boolean requestModeration = true;
    private Integer participantLimit = 0;


}
