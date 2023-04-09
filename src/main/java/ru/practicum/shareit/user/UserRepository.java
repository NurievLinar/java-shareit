package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {

    User addNewUser(User user);

    List<User> getAllUsers();

    User getUserById(long id);

    void deleteUserById(long userId);
}
