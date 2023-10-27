package ru.practicum.ewm.model.comment;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UpdateCommentRequest {
    @NotBlank
    @Size(min = 1, max = 4096)
    private String text;

}
