package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exception.DuplicateEmailException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private long generatorId = 0;

    @Override
    public User addNewUser(User user) {
        validUserEmail(user);
        setId(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(long userId) {
        return users.get(userId);
    }

    @Override
    public void deleteUserById(long userId) {
        users.remove(userId);
    }

    private void setId(User user) {
        if (user.getId() == null) {
            ++generatorId;
            user.setId(generatorId);
        }
    }

    private void validUserEmail(User user) {
        for (User user1 : getAllUsers()) {
            if (user1.getEmail().equals(user.getEmail())) {
                if (!user1.getId().equals(user.getId())) {
                    throw new DuplicateEmailException("Пользователь с таким Email уже существует");
                }
            }
        }
    }


}
