package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .request(itemDto.getRequest() == null ? null : ItemRequest.builder().id(itemDto.getRequest()).build())
                .build();
    }

    public static Item matchItem(ItemDto itemDto, Item item) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName() == null ? item.getName() : itemDto.getName())
                .description(itemDto.getDescription() == null ? item.getDescription() : itemDto.getDescription())
                .available(itemDto.getAvailable() == null ? item.getAvailable() : itemDto.getAvailable())
                .request(itemDto.getRequest() == null ?
                        item.getRequest() : ItemRequest.builder().id(itemDto.getRequest()).build())
                .build();
    }

    public static ItemDto toItemBookingDto(Item item, List<CommentDto> comments, Booking last, Booking next) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(last != null ? BookingMapper.toLastBooking(last) : null)
                .nextBooking(last != null ? BookingMapper.toNextBooking(next) : null)
                .comments(comments)
                .build();
    }
}
