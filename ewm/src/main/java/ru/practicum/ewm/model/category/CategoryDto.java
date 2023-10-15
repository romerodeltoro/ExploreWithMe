package ru.practicum.ewm.model.category;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    @NotBlank(message = "Поле name не может быть пустым")
    @Size(min = 1, max = 50)
    private String name;
}
