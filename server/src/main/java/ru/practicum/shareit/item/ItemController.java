package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addNewItemDto(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody ItemDto itemDto) {
        log.info("Получен запрос 'POST /items'");
        return itemService.addNewItemDto(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("Получен запрос 'PATCH /items/{}'", itemId);
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long itemId) {
        log.info(String.format("Получен запрос 'GET /items/%d'", itemId));
        return itemService.getItemDtoById(userId, itemId);
    }

    @GetMapping()
    public List<ItemDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestParam(defaultValue = "0") Long from,
                                       @RequestParam(defaultValue = "10") Long size) {
        log.info("Получен запрос 'GET /items/'");
        return itemService.getOwnerItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam String text,
                                     @RequestParam(defaultValue = "0") Long from,
                                     @RequestParam(defaultValue = "10") Long size) {
        log.info("Получен запрос 'GET /items/search?text = {}'", text);
        return itemService.searchItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody CommentDto commentDto) {
        log.info("Получен запрос 'POST /{itemId}/comment'");
        return itemService.addComment(userId, itemId, commentDto);
    }
}
