package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        itemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAllUserRequest(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_IdOrderByCreatedDesc(userId);
        if (itemRequests.isEmpty()) return Collections.emptyList();
        List<ItemRequestDto> itemRequestDtos = itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        List<Long> requestIdList = itemRequestDtos.stream()
                .map(ItemRequestDto::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByRequest_IdIn(requestIdList);
        for (ItemRequestDto itemRequestDto : itemRequestDtos) {
            List<Item> requestItems = items.stream()
                    .filter(item -> item.getRequest().getId().equals(itemRequestDto.getId()))
                    .collect(Collectors.toList());
            if (!requestItems.isEmpty()) {
                List<ItemDto> itemDtos = requestItems.stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList());
                itemRequestDto.setItems(itemDtos);
            }
        }
        return itemRequestDtos;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Long from, Long size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        PageRequest pageRequest = PageRequest.of(
                from.intValue(), size.intValue(), sort);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_IdIsNot(userId, pageRequest);
        List<ItemRequestDto> itemRequestDtos = itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        List<Long> requestIdList = itemRequestDtos.stream()
                .map(ItemRequestDto::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByRequest_IdIn(requestIdList);
        for (ItemRequestDto itemRequestDto : itemRequestDtos) {
            List<Item> requestItems = items.stream()
                    .filter(item -> item.getRequest().getId().equals(itemRequestDto.getId()))
                    .collect(Collectors.toList());
            if (!requestItems.isEmpty()) {
                List<ItemDto> itemDtos = requestItems.stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList());
                itemRequestDto.setItems(itemDtos);
            }
        }
        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        ItemRequest itemRequest = itemRequestRepository.findItemRequestById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("Запрос не найден"));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        List<Item> items = itemRepository.findAllByRequest_Id(itemRequestDto.getId());
        if (!items.isEmpty()) {
            List<ItemDto> itemDtos = items.stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
            itemRequestDto.setItems(itemDtos);
        }
        return itemRequestDto;
    }
}