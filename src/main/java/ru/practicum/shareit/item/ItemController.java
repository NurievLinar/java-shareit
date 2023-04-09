package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.UserNotFoundException;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;


    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addNewItemDto(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody ItemDto itemDto) {
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
    public List<ItemDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос 'GET /items/'");
        return itemService.getOwnerItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam String text) {
        log.info("Получен запрос 'GET /items/search?text = {}'", text);
        return itemService.searchItems(userId, text);

    }
}
