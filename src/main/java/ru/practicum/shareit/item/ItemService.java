package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addNewItemDto(ItemDto itemDto, Long userId);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    ItemDto getItemDtoById(Long id, Long itemId);

    List<ItemDto> getOwnerItems(Long userId);

    List<ItemDto> searchItems(Long userId, String text);

    CommentDto comment(Long userId, Long itemId, CommentDto commentDto);
}
