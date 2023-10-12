package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.model.user.UserDto;
import ru.practicum.ewm.service.UserService;
import ru.practicum.ewm.storage.UserRepository;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getUsers(List<Long> ids, Integer from, Integer size) {
        if(ids == null) {
            return userRepository.findAll(PageRequest.of(from, size)).toList();
        } else {
            Pageable pageable = PageRequest.of(from, size);
            return userRepository.findAllByIdAndPage(ids, pageable).toList();
        }
    }

    @Override
    @Transactional
    public User createUser(UserDto userDto) {
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        log.info("Создан пользователь - {}", user);
        return user;
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        findUserByIdOrElseThrow(userId);
        userRepository.deleteById(userId);
        log.info("Пользователь с id '{}' - удален", userId);
    }

    private User findUserByIdOrElseThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователя с id %d нет в базе", id)
        ));
    }
}
