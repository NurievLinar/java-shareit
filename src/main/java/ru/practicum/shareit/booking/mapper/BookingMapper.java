package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.LastBookingDto;
import ru.practicum.shareit.booking.dto.NextBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.user.dto.UserInfoDto;

public class BookingMapper {

    public static BookingInfoDto toBookingInfoDto(Booking booking) {
        return BookingInfoDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemInfoDto.builder()
                        .id(booking.getItem().getId())
                        .name(booking.getItem().getName())
                        .build())
                .booker(UserInfoDto.builder()
                        .id(booking.getBooker().getId())
                        .build())
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
    }

    public static LastBookingDto toLastBooking(Booking last) {
        return LastBookingDto.builder()
                .id(last.getId())
                .bookerId(last.getBooker().getId())
                .start(last.getStart())
                .end(last.getEnd())
                .build();
    }
    public static NextBookingDto toNextBooking(Booking next) {
        return NextBookingDto.builder()
                .id(next.getId())
                .bookerId(next.getBooker().getId())
                .start(next.getStart())
                .end(next.getEnd())
                .build();
    }
}