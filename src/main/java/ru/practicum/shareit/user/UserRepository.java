package ru.practicum.shareit.user;

import ru.practicum.shareit.user.exception.DuplicateEmailException;

import java.util.List;

public interface UserRepository {

    User addNewUser(User user) throws DuplicateEmailException;

    List<User> getAllUsers();

    User getUserById(long id);

    void deleteUserById(long userId);
}
