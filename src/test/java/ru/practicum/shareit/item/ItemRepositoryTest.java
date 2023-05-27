package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Test
    void searchAvailableByText() {
        User owner = User.builder()
                .name("user1")
                .email("user1@email.com")
                .build();

        owner = userRepository.save(owner);

        Item item = Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        item = itemRepository.save(item);

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Item> items = itemRepository.searchAvailableByText("name", pageRequest);
        Assertions.assertTrue(items.get(0).getName().contains(item.getName()));
    }
}
