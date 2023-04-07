package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.UserNotFoundException;

import java.util.List;

public interface ItemService {
    ItemDto addNewItemDto(ItemDto itemDto, Long userId) throws UserNotFoundException;

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) throws UserNotFoundException, ItemNotFoundException;

    ItemDto getItemDtoById(Long id, Long itemId) throws UserNotFoundException;

    List<ItemDto> getOwnerItems(Long userId) throws UserNotFoundException;

    List<ItemDto> searchItems(Long userId, String text) throws UserNotFoundException;
}
