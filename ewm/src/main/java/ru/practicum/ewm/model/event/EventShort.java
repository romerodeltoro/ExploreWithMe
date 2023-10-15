package ru.practicum.ewm.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.model.LocationDto;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.model.user.UserShort;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventShort {

    private Long id;
    @NotBlank
    private String title;
    @NotBlank
    private String annotation;
    @NotBlank
    private Category category;
    @NotBlank
    private Boolean paid;
    @NotBlank
    private String eventDate;
    private Integer confirmedRequests;
    @NotBlank
    private UserShort initiator;
    private Integer views;
}
