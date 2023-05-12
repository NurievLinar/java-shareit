package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto addNewUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        userRepository.addNewUser(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.getAllUsers();
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(long userId) {
        User repoUser = userRepository.getUserById(userId);
        validUser(repoUser);
        return UserMapper.toUserDto(repoUser);
    }

    @Override
    public void deleteUserById(long userId) {
        userRepository.deleteUserById(userId);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        userDto.setId(userId);
        User repoUser = userRepository.getUserById(userId);
        validUser(repoUser);
        User user = UserMapper.updateUser(userDto, repoUser);
        userRepository.addNewUser(user);
        return UserMapper.toUserDto(user);
    }

    private void validUser(User repoUser) {
        if (repoUser == null) {
            throw new ValidationException("Такого пользователя не существует");
        }
    }
}