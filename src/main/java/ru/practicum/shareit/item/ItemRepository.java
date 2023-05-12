package ru.practicum.shareit.item;

import java.util.List;

public interface ItemRepository {
    Item addNewItem(Long userId, Item item);

    Item getItemByTwoId(Long userId, Long itemId);

    List<Item> getOwnerItems(Long userId);

    List<Item> searchItems(String text);

    Item getItemById(Long itemId);
}
