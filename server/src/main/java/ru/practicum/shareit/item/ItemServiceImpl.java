package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.LastBookingDto;
import ru.practicum.shareit.booking.dto.NextBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.InvalidCommentException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    public ItemDto addNewItemDto(ItemDto itemDto, Long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        item = itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Item repoItem = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
        if (!repoItem.getOwner().getId().equals(owner.getId())) throw new ItemNotFoundException("Вещь не найдена");
        itemDto.setId(itemId);
        Item item = ItemMapper.matchItem(itemDto, repoItem);
        item.setOwner(owner);
        itemRepository.save(item);
        item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemDtoById(Long userId, Long itemId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Item repoItem = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
        User owner = repoItem.getOwner();
        ItemDto itemDto = ItemMapper.toItemDto(repoItem);
        itemDto.setOwner(owner.getId());
        List<Comment> commentList = commentRepository.findAllByItem_Id(itemId);
        List<CommentDto> commentDtos = commentList.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemDto.setComments(commentDtos);
        if (!user.getId().equals(owner.getId())) return itemDto;
        Sort sortDesc = Sort.by(Sort.Direction.DESC, "end");
        Optional<Booking> lastBooking = bookingRepository.findTop1BookingByItem_IdAndStartIsBeforeAndStatusIs(
                itemId, LocalDateTime.now(), Status.APPROVED, sortDesc);
        itemDto.setLastBooking(lastBooking.isEmpty() ? null : LastBookingDto.builder()
                .id(lastBooking.get().getId())
                .bookerId(lastBooking.get().getBooker().getId())
                .start(lastBooking.get().getStart())
                .end(lastBooking.get().getEnd())
                .build());
        Sort sortAsc = Sort.by(Sort.Direction.ASC, "start");
        Optional<Booking> nextBooking = bookingRepository.findTop1BookingByItem_IdAndStartIsAfterAndStatusIs(
                itemId, LocalDateTime.now(), Status.APPROVED, sortAsc);
        itemDto.setNextBooking(nextBooking.isEmpty() ? null : NextBookingDto.builder()
                .id(nextBooking.get().getId())
                .bookerId(nextBooking.get().getBooker().getId())
                .start(nextBooking.get().getStart())
                .end(nextBooking.get().getEnd())
                .build());
        return itemDto;
    }

    @Override
    public List<ItemDto> getOwnerItems(Long userId, Long from, Long size) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        PageRequest pageRequest = PageRequest.of(from.intValue() / size.intValue(), size.intValue());

        Collection<Item> itemList = itemRepository.findAllByOwner_Id(userId, pageRequest)
                .stream().sorted(Comparator.comparing(Item::getId)).collect(Collectors.toList());
        Set<Long> itemsIds = itemList.stream().map(Item::getId).collect(Collectors.toSet());
        Map<Long, List<Comment>> comments = commentRepository
                .findAllByItem_IdInOrderByItem_Id(itemsIds)
                .stream()
                .collect(Collectors.groupingBy(it -> it.getItem().getId()));
        Map<Long, List<Booking>> bookingsItem = bookingRepository
                .findAllByItem_IdInOrderByStartAsc(itemsIds)
                .stream()
                .collect(Collectors.groupingBy(it -> it.getItem().getId()));
        List<ItemDto> itemDtoList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (Item item : itemList) {
            Booking lastBooking = null;
            Booking nextBooking = null;
            if (bookingsItem.get(item.getId()) != null) {
                List<Booking> bookingList = bookingsItem.get(item.getId());
                lastBooking = bookingList.stream()
                        .filter(b -> b.getEnd().isBefore(now))
                        .findFirst()
                        .orElse(null);

                nextBooking = bookingList.stream()
                        .filter(b -> b.getStart().isAfter(now))
                        .reduce((first, second) -> first.getStart().isBefore(second.getStart()) ? first : second)
                        .orElse(null);
            }
            ItemDto itemDto = ItemMapper.toItemBookingDto(
                    item,
                    comments.getOrDefault(item.getId(), List.of()).stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()),
                    lastBooking,
                    nextBooking
            );
            itemDtoList.add(itemDto);
        }
        return itemDtoList;
    }

    @Override
    public List<ItemDto> searchItems(Long userId, String text, Long from, Long size) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        if (text.isEmpty()) return Collections.emptyList();
        PageRequest pageRequest = PageRequest.of(from.intValue() / size.intValue(), size.intValue());
        List<Item> searchItemList = itemRepository.searchAvailableByText(text, pageRequest);
        List<ItemDto> searchItemDto = new ArrayList<>();
        for (Item item : searchItemList) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            itemDto.setOwner(item.getOwner().getId());
            searchItemDto.add(itemDto);
        }
        return searchItemDto.isEmpty() ? Collections.emptyList() : searchItemDto;
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
        User author = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Sort sortDesc = Sort.by(Sort.Direction.DESC, "end");
        Booking booking = bookingRepository.findTop1BookingByItem_IdAndBooker_IdAndEndIsBeforeAndStatusIs(
                itemId, userId, LocalDateTime.now(), Status.APPROVED, sortDesc).orElseThrow(
                () -> new InvalidCommentException("no booking for comment"));
        Comment comment = CommentMapper.toComment(commentDto, item, author, LocalDateTime.now());
        comment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }
}