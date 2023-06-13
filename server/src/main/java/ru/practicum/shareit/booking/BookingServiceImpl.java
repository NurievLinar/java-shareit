package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.exeptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exeptions.InvalidStatusException;
import ru.practicum.shareit.booking.exeptions.NotAvailableException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingInfoDto create(Long userId, BookingDto bookingDto) {
        Long itemId = bookingDto.getItemId();
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
        if (!item.getAvailable()) throw new NotAvailableException("Вещь не доступна");

        User booker = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        if (booker.getId().equals(item.getOwner().getId())) throw new UserNotFoundException("Пользователь не найден");

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        booking = bookingRepository.save(booking);
        return BookingMapper.toBookingInfoDto(booking);
    }

    @Override
    public BookingInfoDto approve(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование не найдено"));
        Item item = booking.getItem();
        if (!userId.equals(item.getOwner().getId())) throw new UserNotFoundException("Пользователь не найден");
        if (Status.APPROVED.equals(booking.getStatus()) ||
                Status.REJECTED.equals(booking.getStatus()))
            throw new InvalidStatusException("Статус не требует изменений");
        if (approved != null) booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        booking = bookingRepository.save(booking);
        return BookingMapper.toBookingInfoDto(booking);
    }

    @Override
    public BookingInfoDto get(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование не найдено"));
        Item item = booking.getItem();
        if (!userId.equals(item.getOwner().getId()) && !userId.equals(booking.getBooker().getId())) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        return BookingMapper.toBookingInfoDto(booking);
    }

    @Override()
    public List<BookingInfoDto> get(Long userId, String value, Long from, Long size) {
        State state = State.valueOf(value);
        User booker = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        PageRequest pageRequest = PageRequest.of(from.intValue() / size.intValue(), size.intValue());
        List<Booking> bookings = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBooker_IdOrderByStartDesc(userId, pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBooker_IdAndEndIsBefore(userId, LocalDateTime.now(), sort, pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBooker_IdAndStartIsAfter(userId, LocalDateTime.now(), sort, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository
                        .findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(
                                userId, LocalDateTime.now(), LocalDateTime.now(), sort, pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBooker_IdAndStatus(userId, Status.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBooker_IdAndStatus(userId, Status.REJECTED, pageRequest);
                break;
            default:
                throw new InvalidStatusException("Unknown state: " + value);
        }

        return bookings.isEmpty() ? Collections.emptyList() : bookings.stream()
                .map(BookingMapper::toBookingInfoDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingInfoDto> getByOwner(Long userId, String value, Long from, Long size) {
        State state = State.valueOf(value);
        User owner = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        PageRequest pageRequest = PageRequest.of(from.intValue() / size.intValue(), size.intValue());
        List<Booking> bookings = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(userId, pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItem_Owner_IdAndEndIsBefore(userId, LocalDateTime.now(), sort, pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStartIsAfter(userId, LocalDateTime.now(), sort, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository
                        .findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
                                userId, LocalDateTime.now(), LocalDateTime.now(), sort, pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStatus(userId, Status.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStatus(userId, Status.REJECTED, pageRequest);
                break;
            default:
                throw new InvalidStatusException("Unknown state: " + value);
        }

        return bookings.isEmpty() ? Collections.emptyList() : bookings.stream()
                .map(BookingMapper::toBookingInfoDto)
                .collect(Collectors.toList());
    }
}
