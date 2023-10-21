package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.model.user.UserDto;
import ru.practicum.ewm.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated
public class UsersAdminController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getUsers(
            @RequestParam(name = "ids", required = false) List<Long> ids,
            @RequestParam(name = "from", defaultValue = "0", required = false) @Min(0) Integer from,
            @RequestParam(name = "size", defaultValue = "10", required = false) @Min(1) Integer size) {
        return ResponseEntity.ok().body(userService.getUsers(ids, from, size));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid UserDto userDto) {
        return ResponseEntity.status(201).body(userService.createUser(userDto));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.status(204).build();
    }

}
