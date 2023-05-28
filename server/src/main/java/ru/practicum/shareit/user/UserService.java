package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addNewUser(UserDto userDto);

    List<UserDto> getAllUsers();

    UserDto getUserById(long id);

    void deleteUserById(long userId);

    UserDto updateUser(Long userId, UserDto userDto);
}
