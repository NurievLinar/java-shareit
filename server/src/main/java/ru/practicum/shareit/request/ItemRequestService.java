package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllUserRequest(Long userId);

    List<ItemRequestDto> getAllRequests(Long userId, Long from, Long size);

    ItemRequestDto getRequestById(Long userId, Long requestId);
}