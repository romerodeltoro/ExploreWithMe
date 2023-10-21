package ru.practicum.ewm.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.model.location.LocationDto;

import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminRequest {

    @Size(min = 3, max = 120)
    private String title;
    @Size(min = 20, max = 2000)
    private String annotation;
    private Long category;
    @Size(min = 20, max = 7000)
    private String description;
    private String eventDate;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;

}
