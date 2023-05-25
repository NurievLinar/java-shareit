package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;

import java.util.List;

public interface BookingService {

    BookingInfoDto create(Long userId, BookingDto bookingDto);

    BookingInfoDto approve(Long userId, Long bookingId, Boolean approved);

    BookingInfoDto get(Long userId, Long bookingId);

    List<BookingInfoDto> get(Long userId, String state, Long from, Long size);

    List<BookingInfoDto> getByOwner(Long userId, String state, Long from, Long size);
}