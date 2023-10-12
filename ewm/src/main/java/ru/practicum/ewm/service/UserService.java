package ru.practicum.ewm.service;

import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.model.user.UserDto;

import java.util.List;

public interface UserService {
    List<User> getUsers(List<Long> ids, Integer from, Integer size);

    User createUser(UserDto userDto);

    void deleteUser(Long userId);
}
