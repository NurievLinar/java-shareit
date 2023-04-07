package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exception.DuplicateEmailException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public interface UserService {
    UserDto addNewUser(UserDto userDto) throws DuplicateEmailException;

    List<UserDto> getAllUsers();

    UserDto getUserById(long id);

    void deleteUserById(long userId);

    UserDto updateUser(Long userId, UserDto userDto) throws DuplicateEmailException;
}
