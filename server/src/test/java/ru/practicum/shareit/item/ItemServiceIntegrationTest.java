package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIntegrationTest {

    @Autowired
    private final ItemService itemService;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        User owner = User.builder()
                .name("owner")
                .email("owner@email.com")
                .build();
        userRepository.save(owner);

        Item item = Item.builder()
                .name("item1")
                .description("item1 desc")
                .available(true)
                .owner(owner)
                .build();

        User booker = User.builder()
                .name("booker")
                .email("booker@email.com")
                .build();

        LocalDateTime created = LocalDateTime.now();

        Booking lastBooking = Booking.builder()
                .start(created.minusMonths(5))
                .end(created.minusMonths(4))
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        Booking nextBooking = Booking.builder()
                .start(created.plusDays(1L))
                .end(created.plusDays(2L))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();

        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
        bookingRepository.save(lastBooking);
        bookingRepository.save(nextBooking);

    }

    @AfterEach
    void tearDown() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DirtiesContext
    @Test
    void addNewItemDto() {
        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        itemDto = itemService.addNewItemDto(itemDto, 1L);

        assertThat(itemDto, is(notNullValue()));
        assertThat(itemDto.getId(), is(2L));
    }

    @DirtiesContext
    @Test
    void getItemDtoById() {
        ItemDto itemDto = itemService.getItemDtoById(1L, 1L);
        assertThat(itemDto.getId(), is(1L));
        assertThat(itemDto.getLastBooking().getId(), is(1L));
    }
}
