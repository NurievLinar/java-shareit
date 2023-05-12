package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, List<Item>> items = new HashMap<>();

    private long generatorId = 0;

    @Override
    public Item addNewItem(Long userId, Item item) {
        setId(item);
        List<Item> itemList = items.get(userId);
        if (itemList == null) {
            itemList = new ArrayList<>();
            itemList.add(item);
            items.put(userId, itemList);
            return item;
        }
        itemList.removeIf(element -> element.getId().equals(item.getId()));
        itemList.add(item);
        items.put(userId, itemList);
        return item;
    }

    @Override
    public Item getItemByTwoId(Long userId, Long itemId) {
        List<Item> itemList = items.get(userId);
        if (itemList == null) {
            return null;
        }
        Item item = null;
        for (Item currentItem : itemList) {
            if (currentItem.getId().equals(itemId)) {
                item = currentItem;
            }
        }
        return item;
    }

    @Override
    public List<Item> getOwnerItems(Long userId) {
        return items.get(userId);
    }

    @Override
    public List<Item> searchItems(String text) {
        String substring = text.toLowerCase();
        List<Item> searchItemsList = new ArrayList<>();
        for (List<Item> itemList : items.values()) {
            for (Item item : itemList) {
                if (item.getAvailable() && (item.getName().toLowerCase().contains(substring) ||
                        item.getDescription().toLowerCase().contains(substring))) {
                    searchItemsList.add(item);
                }
            }
        }
        return searchItemsList;
    }

    @Override
    public Item getItemById(Long itemId) {
        Item itemById = null;
        for (List<Item> itemList : items.values()) {
            for (Item item : itemList) {
                if (item.getId().equals(itemId)) {
                    itemById = item;
                }
            }
        }
        return itemById;
    }

    private void setId(Item item) {
        if (item.getId() == null) {
            ++generatorId;
            item.setId(generatorId);
        }
    }
}
