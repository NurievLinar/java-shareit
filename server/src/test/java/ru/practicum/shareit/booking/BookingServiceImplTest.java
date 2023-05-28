package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.exeptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exeptions.InvalidDateTimeException;
import ru.practicum.shareit.booking.exeptions.InvalidStatusException;
import ru.practicum.shareit.booking.exeptions.NotAvailableException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.PaginationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    @Autowired
    private final BookingServiceImpl bookingService;

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final ItemRepository itemRepository;

    @MockBean
    private final BookingRepository bookingRepository;

    @Test
    void create() {
        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);

        BookingDto bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();

        User owner = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@email.com")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        User booker = User.builder()
                .id(3L)
                .name("user3")
                .email("user3@email.com")
                .build();
        when(userRepository.findById(3L))
                .thenReturn(Optional.of(booker));

        Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingInfoDto bookingInfoDto = bookingService.create(3L, bookingDto);
        assertThat(bookingInfoDto, is(notNullValue()));
    }

    @Test
    void throwItemNotFoundException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);

        BookingDto bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();

        ItemNotFoundException invalidItemIdException;

        when(itemRepository.findById(1L))
                .thenReturn(Optional.empty());
        invalidItemIdException = Assertions.assertThrows(ItemNotFoundException.class,
                () -> bookingService.create(3L, bookingDto));
        assertThat(invalidItemIdException.getMessage(), is("Вещь не найдена"));
    }

    @Test
    void throwNotAvailableException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);

        BookingDto bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();

        User owner = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@email.com")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(false)
                .owner(owner)
                .build();
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        NotAvailableException notAvailableException;
        notAvailableException = Assertions.assertThrows(NotAvailableException.class,
                () -> bookingService.create(3L, bookingDto));
        assertThat(notAvailableException.getMessage(), is("Вещь не доступна"));
    }

    @Test
    void throwInvalidDateTimeException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);

        BookingDto bookingDto = BookingDto.builder()
                .start(end)
                .end(start)
                .itemId(1L)
                .build();

        User owner = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@email.com")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        InvalidDateTimeException invalidDateTimeException;
        invalidDateTimeException = Assertions.assertThrows(InvalidDateTimeException.class,
                () -> bookingService.create(3L, bookingDto));
        assertThat(invalidDateTimeException.getMessage(), is("Неверное время"));
    }

    @Test
    void throwUserNotFoundException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);

        BookingDto bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();

        User owner = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@email.com")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        User booker = User.builder()
                .id(3L)
                .name("user3")
                .email("user3@email.com")
                .build();
        when(userRepository.findById(3L))
                .thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException;
        userNotFoundException = Assertions.assertThrows(UserNotFoundException.class,
                () -> bookingService.create(3L, bookingDto));
        assertThat(userNotFoundException.getMessage(), is("Пользователь не найден"));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        userNotFoundException = Assertions.assertThrows(UserNotFoundException.class,
                () -> bookingService.create(1L, bookingDto));
        assertThat(userNotFoundException.getMessage(), is("Пользователь не найден"));

        Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        userNotFoundException = Assertions.assertThrows(UserNotFoundException.class,
                () -> bookingService.approve(3L, 1L, true));
        assertThat(userNotFoundException.getMessage(), is("Пользователь не найден"));

        userNotFoundException = Assertions.assertThrows(UserNotFoundException.class,
                () -> bookingService.get(2L, 1L));
        assertThat(userNotFoundException.getMessage(), is("Пользователь не найден"));

        when(userRepository.findById(2L))
                .thenReturn(Optional.empty());

        userNotFoundException = Assertions.assertThrows(UserNotFoundException.class,
                () -> bookingService.get(2L, "ALL", 0L, 10L));
        assertThat(userNotFoundException.getMessage(), is("Пользователь не найден"));

        userNotFoundException = Assertions.assertThrows(UserNotFoundException.class,
                () -> bookingService.getByOwner(2L, "ALL", 0L, 10L));
        assertThat(userNotFoundException.getMessage(), is("Пользователь не найден"));
    }

    @Test
    void approve() {
        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);

        User owner = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@email.com")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        User booker = User.builder()
                .id(3L)
                .name("user3")
                .email("user3@email.com")
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingInfoDto bookingInfoDto = bookingService.approve(1L, 1L, true);
        assertThat(bookingInfoDto, is(notNullValue()));

        booking.setStatus(Status.WAITING);
        bookingInfoDto = bookingService.approve(1L, 1L, null);
        assertThat(bookingInfoDto, is(notNullValue()));

        booking.setStatus(Status.WAITING);
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(
                        invocation -> {
                            Booking invoc = invocation.getArgument(0, Booking.class);
                            invoc.setStatus(Status.APPROVED);
                            return invoc;
                        }
                );

        bookingInfoDto = bookingService.approve(1L, 1L, true);
        assertThat(bookingInfoDto, is(notNullValue()));

        booking.setStatus(Status.WAITING);
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(
                        invocation -> {
                            Booking invoc = invocation.getArgument(0, Booking.class);
                            invoc.setStatus(Status.REJECTED);
                            return invoc;
                        }
                );
        bookingInfoDto = bookingService.approve(1L, 1L, false);
        assertThat(bookingInfoDto, is(notNullValue()));
        Assertions.assertEquals(bookingInfoDto.getStatus(), Status.REJECTED);
    }

    @Test
    void throwBookingNotFoundException() {
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.empty());

        BookingNotFoundException bookingNotFoundException;

        bookingNotFoundException = Assertions.assertThrows(BookingNotFoundException.class,
                () -> bookingService.approve(1L, 1L, true));
        assertThat(bookingNotFoundException.getMessage(), is("Бронирование не найдено"));

        bookingNotFoundException = Assertions.assertThrows(BookingNotFoundException.class,
                () -> bookingService.get(1L, 1L));
        assertThat(bookingNotFoundException.getMessage(), is("Бронирование не найдено"));
    }

    @Test
    void throwInvalidStatusException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);

        User owner = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@email.com")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        User booker = User.builder()
                .id(3L)
                .name("user3")
                .email("user3@email.com")
                .build();

        final Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        InvalidStatusException invalidStatusException;

        invalidStatusException = Assertions.assertThrows(InvalidStatusException.class,
                () -> bookingService.approve(1L, 1L, true));
        assertThat(invalidStatusException.getMessage(), is("Статус не требует изменений"));

        booking.setStatus(Status.REJECTED);
        invalidStatusException = Assertions.assertThrows(InvalidStatusException.class,
                () -> bookingService.approve(1L, 1L, true));
        assertThat(invalidStatusException.getMessage(), is("Статус не требует изменений"));

        invalidStatusException = Assertions.assertThrows(InvalidStatusException.class,
                () -> bookingService.get(1L, "value", 0L, 10L));
        assertThat(invalidStatusException.getMessage(), is("Unknown state: value"));
    }

    @Test
    void getBooking() {
        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);

        User owner = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@email.com")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        User booker = User.builder()
                .id(3L)
                .name("user3")
                .email("user3@email.com")
                .build();

        final Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        BookingInfoDto bookingInfoDto = bookingService.get(1L, 1L);
        assertThat(bookingInfoDto, is(notNullValue()));

        bookingInfoDto = bookingService.get(3L, 1L);
        assertThat(bookingInfoDto, is(notNullValue()));
    }

    @Test
    void getAllByBookerAndStatus() {
        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);

        User owner = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@email.com")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        User booker = User.builder()
                .id(3L)
                .name("user3")
                .email("user3@email.com")
                .build();

        when(userRepository.findById(3L))
                .thenReturn(Optional.of(booker));

        Booking booking1 = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
        when(bookingRepository.findAllByBooker_IdAndStatus(any(), any(), any()))
                .thenReturn(List.of(booking1));
        List<BookingInfoDto> bookingInfoDtoList = bookingService.get(3L, "ALL", 0L, 10L);
        Assertions.assertTrue(bookingInfoDtoList.isEmpty());

        start = LocalDateTime.now().minusDays(2L);
        end = LocalDateTime.now().minusDays(1L);
        Booking booking2 = Booking.builder()
                .id(2L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
        when(bookingRepository.findAllByBooker_IdAndEndIsBefore(any(), any(), any(), any()))
                .thenReturn(List.of(booking2));
        bookingInfoDtoList = bookingService.get(3L, "PAST", 0L, 10L);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());

        start = LocalDateTime.now().plusDays(1L);
        end = LocalDateTime.now().plusDays(2L);
        Booking booking3 = Booking.builder()
                .id(3L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
        when(bookingRepository.findAllByBooker_IdAndStartIsAfter(any(), any(), any(), any()))
                .thenReturn(List.of(booking3));
        bookingInfoDtoList = bookingService.get(3L, "FUTURE", 0L, 10L);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());

        start = LocalDateTime.now().minusDays(1L);
        end = LocalDateTime.now().plusDays(2L);
        Booking booking4 = Booking.builder()
                .id(4L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
        when(bookingRepository.findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any(), any()))
                .thenReturn(List.of(booking4));
        bookingInfoDtoList = bookingService.get(3L, "CURRENT", 0L, 10L);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());

        start = LocalDateTime.now().plusDays(1L);
        end = LocalDateTime.now().plusDays(2L);
        Booking booking5 = Booking.builder()
                .id(5L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        when(bookingRepository.findAllByBooker_IdAndStatus(any(Long.class), any(Status.class), any(PageRequest.class)))
                .thenAnswer(
                        invocation -> {
                            Long userId = invocation.getArgument(0, Long.class);
                            Status status = invocation.getArgument(1, Status.class);
                            if (status.equals(Status.WAITING) && userId.equals(3L)) {
                                booking5.setStatus(Status.WAITING);
                                return List.of(booking5);
                            }
                            if (status.equals(Status.REJECTED) && userId.equals(3L)) {
                                booking5.setStatus(Status.REJECTED);
                                return List.of(booking5);
                            }
                            return Collections.emptyList();
                        }

                );

        User booker6 = User.builder()
                .id(6L)
                .name("user6")
                .email("user6@email.com")
                .build();

        when(userRepository.findById(6L))
                .thenReturn(Optional.of(booker6));

        bookingInfoDtoList = bookingService.get(3L, "WAITING", 0L, 10L);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());
        assertThat(bookingInfoDtoList.get(0).getStatus(), is(Status.WAITING));

        bookingInfoDtoList = bookingService.get(3L, "REJECTED", 0L, 10L);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());
        assertThat(bookingInfoDtoList.get(0).getStatus(), is(Status.REJECTED));

        bookingInfoDtoList = bookingService.get(6L, "WAITING", 0L, 10L);
        Assertions.assertTrue(bookingInfoDtoList.isEmpty());
    }

    @Test
    void throwPaginationException() {
        User user = User.builder()
                .id(3L)
                .name("user3")
                .email("user3@email.com")
                .build();

        when(userRepository.findById(3L))
                .thenReturn(Optional.of(user));

        PaginationException invalidPageParamsException;

        invalidPageParamsException = Assertions.assertThrows(PaginationException.class,
                () -> bookingService.get(3L, "ALL", -1L, 10L));
        assertThat(invalidPageParamsException.getMessage(), is("Ошибка пагинации"));

        invalidPageParamsException = Assertions.assertThrows(PaginationException.class,
                () -> bookingService.get(3L, "ALL", 0L, 0L));
        assertThat(invalidPageParamsException.getMessage(), is("Ошибка пагинации"));

        invalidPageParamsException = Assertions.assertThrows(PaginationException.class,
                () -> bookingService.getByOwner(3L, "ALL", -1L, 10L));
        assertThat(invalidPageParamsException.getMessage(), is("Ошибка пагинации"));

        invalidPageParamsException = Assertions.assertThrows(PaginationException.class,
                () -> bookingService.getByOwner(3L, "ALL", 0L, 0L));
        assertThat(invalidPageParamsException.getMessage(), is("Ошибка пагинации"));
    }

    @Test
    void getByOwner() {
        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);

        User owner = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@email.com")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        User booker = User.builder()
                .id(3L)
                .name("user3")
                .email("user3@email.com")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        Booking booking1 = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        when(bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(any(), any()))
                .thenReturn(List.of(booking1));

        List<BookingInfoDto> bookingInfoDtoList = bookingService.getByOwner(1L, "ALL", 0L, 10L);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());

        start = LocalDateTime.now().minusDays(2L);
        end = LocalDateTime.now().minusDays(1L);
        Booking booking2 = Booking.builder()
                .id(2L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
        when(bookingRepository.findAllByItem_Owner_IdAndEndIsBefore(any(), any(), any(), any()))
                .thenReturn(List.of(booking2));
        bookingInfoDtoList = bookingService.getByOwner(1L, "PAST", 0L, 10L);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());

        start = LocalDateTime.now().plusDays(1L);
        end = LocalDateTime.now().plusDays(2L);
        Booking booking3 = Booking.builder()
                .id(3L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
        when(bookingRepository.findAllByItem_Owner_IdAndStartIsAfter(any(), any(), any(), any()))
                .thenReturn(List.of(booking3));
        bookingInfoDtoList = bookingService.getByOwner(1L, "FUTURE", 0L, 10L);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());

        start = LocalDateTime.now().minusDays(1L);
        end = LocalDateTime.now().plusDays(2L);
        Booking booking4 = Booking.builder()
                .id(4L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
        when(bookingRepository.findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any(), any()))
                .thenReturn(List.of(booking4));
        bookingInfoDtoList = bookingService.getByOwner(1L, "CURRENT", 0L, 10L);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());

        start = LocalDateTime.now().plusDays(1L);
        end = LocalDateTime.now().plusDays(2L);
        Booking booking5 = Booking.builder()
                .id(5L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        when(bookingRepository.findAllByItem_Owner_IdAndStatus(any(Long.class), any(Status.class), any(PageRequest.class)))
                .thenAnswer(
                        invocation -> {
                            Long userId = invocation.getArgument(0, Long.class);
                            Status status = invocation.getArgument(1, Status.class);
                            if (status.equals(Status.WAITING) && userId.equals(1L)) {
                                booking5.setStatus(Status.WAITING);
                                return List.of(booking5);
                            }
                            if (status.equals(Status.REJECTED) && userId.equals(1L)) {
                                booking5.setStatus(Status.REJECTED);
                                return List.of(booking5);
                            }
                            return Collections.emptyList();
                        }

                );

        User booker6 = User.builder()
                .id(6L)
                .name("user6")
                .email("user6@email.com")
                .build();

        when(userRepository.findById(6L))
                .thenReturn(Optional.of(booker6));

        bookingInfoDtoList = bookingService.getByOwner(1L, "WAITING", 0L, 10L);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());
        assertThat(bookingInfoDtoList.get(0).getStatus(), is(Status.WAITING));

        bookingInfoDtoList = bookingService.getByOwner(1L, "REJECTED", 0L, 10L);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());
        assertThat(bookingInfoDtoList.get(0).getStatus(), is(Status.REJECTED));

        bookingInfoDtoList = bookingService.getByOwner(6L, "WAITING", 0L, 10L);
        Assertions.assertTrue(bookingInfoDtoList.isEmpty());
    }
}