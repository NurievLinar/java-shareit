package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    @Autowired
    private final UserServiceImpl userService;

    @MockBean
    private final UserRepository userRepository;

    @Test
    void addNewUser() {
        UserDto userDto = UserDto.builder()
                .name("name")
                .email("user@email.com")
                .build();

        User user = User.builder()
                .id(1L)
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();

        when(userRepository.save(any()))
                .thenReturn(user);

        userDto = userService.addNewUser(userDto);
        assertThat(userDto, is(notNullValue()));
    }

    @Test
    void updateUser() {
        UserDto userDto = UserDto.builder()
                .name("name updated")
                .email("userUpdated@email.com")
                .build();

        User user = User.builder()
                .id(1L)
                .name("name")
                .email("user@email.com")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        when(userRepository.save(any()))
                .thenReturn(user);

        userDto = userService.updateUser(1L, userDto);

        assertThat(userDto, is(notNullValue()));
    }

    @Test
    void throwUserNotFoundException() {
        UserDto userDto = UserDto.builder()
                .name("name updated")
                .email("userUpdated@email.com")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        final UserNotFoundException updateException = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUser(1L, userDto)
        );

        final UserNotFoundException getException = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserById(1L)
        );

        assertThat(updateException.getMessage(), is("Пользователь не найден"));
        assertThat(getException.getMessage(), is("Пользователь не найден"));
    }


    @Test
    void getUser() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("user@email.com")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserDto userDto = userService.getUserById(1L);

        assertThat(userDto, is(notNullValue()));
    }

    @Test
    void deleteUserById() {

        userService.deleteUserById(1L);

        verify(userRepository, times(1)).deleteById(1L);

    }

    @Test
    void getAllUsers() {

        List<User> users = List.of(User.builder()
                .id(1L)
                .name("name")
                .email("user@email.com")
                .build());

        when(userRepository.findAll())
                .thenReturn(users);

        List<UserDto> userDtos = userService.getAllUsers();

        assertThat(userDtos, is(notNullValue()));
    }
}
