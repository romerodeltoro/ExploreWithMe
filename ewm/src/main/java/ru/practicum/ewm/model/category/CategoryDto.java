package ru.practicum.ewm.model.category;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
public class CategoryDto {
    @NotBlank(message = "Поле name не может быть пустым")
    @Size(min = 1, max = 50)
    private String name;
}
