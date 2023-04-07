package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.UserNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    public ItemDto addNewItemDto(ItemDto itemDto, Long userId) throws UserNotFoundException {
        User owner = userRepository.getUserById(userId);
        validOwner(owner);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        itemRepository.addNewItem(userId, item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) throws UserNotFoundException, ItemNotFoundException {
        User owner = userRepository.getUserById(userId);
        validOwner(owner);
        Item repoItem = itemRepository.getItemByTwoId(userId, itemId);
        validItem(repoItem);
        itemDto.setId(itemId);
        Item item = ItemMapper.matchItem(itemDto, repoItem);
        item.setOwner(owner);
        itemRepository.addNewItem(userId, item);
        item = itemRepository.getItemByTwoId(userId, itemId);
        return ItemMapper.toItemDto(item);
    }


    @Override
    public ItemDto getItemDtoById(Long userId, Long itemId) throws UserNotFoundException {
        User user = userRepository.getUserById(userId);
        validOwner(user);
        Item repoItem = itemRepository.getItemById(itemId);
        User owner = repoItem.getOwner();
        ItemDto itemDto = ItemMapper.toItemDto(repoItem);
        itemDto.setOwner(owner.getId());
        return itemDto;
    }

    @Override
    public List<ItemDto> getOwnerItems(Long userId) throws UserNotFoundException {
        User owner = userRepository.getUserById(userId);
        validOwner(owner);
        List<Item> itemList = itemRepository.getOwnerItems(userId);
        if (itemList.isEmpty()) {
            return new ArrayList<>();
        }
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemList) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            itemDto.setOwner(userId);
            itemDtoList.add(itemDto);
        }
        return itemDtoList;
    }

    @Override
    public List<ItemDto> searchItems(Long userId, String text) throws UserNotFoundException {
        User owner = userRepository.getUserById(userId);
        validOwner(owner);
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> searchItemList = itemRepository.searchItems(text);
        List<ItemDto> searchItemDto = new ArrayList<>();
        for (Item item : searchItemList) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            itemDto.setOwner(item.getOwner().getId());
            searchItemDto.add(itemDto);
        }
        return searchItemDto.isEmpty() ? Collections.emptyList() : searchItemDto;
    }

    private void validItem(Item repoItem) throws ItemNotFoundException {
        if (repoItem == null) {
            throw new ItemNotFoundException("Пользователь не найден");
        }
    }

    private void validOwner(User owner) throws UserNotFoundException {
        if (owner == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }
}
