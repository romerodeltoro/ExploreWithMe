package ru.practicum.ewm.model.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserDto {
    @NotBlank(message = "Поле name не может быть пустым")
    @Size(min = 2, max = 250)
    private String name;
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна содержать символ @")
    @Size(min = 6, max = 254)
    private String email;
}
