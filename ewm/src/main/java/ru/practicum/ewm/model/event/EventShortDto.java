package ru.practicum.ewm.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.user.UserShort;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {

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
