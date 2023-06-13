package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.user.dto.UserInfoDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private BookingDto bookingDto;

    private BookingInfoDto bookingInfoDto;

    private ItemInfoDto item;

    private UserInfoDto booker;

    private LocalDateTime start;

    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.now().plusDays(1L);

        end = LocalDateTime.now().plusDays(2L);

        bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();

        item = ItemInfoDto.builder()
                .id(1L)
                .name("item created")
                .build();

        booker = UserInfoDto.builder()
                .id(1L)
                .build();

        bookingInfoDto = BookingInfoDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();
    }

    @AfterEach
    void tearDown() {
        start = null;
        end = null;
        bookingDto = null;
        item = null;
        booker = null;
    }

    @Test
    void create() throws Exception {
        when(bookingService.create(1L, bookingDto))
                .thenReturn(bookingInfoDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingInfoDto.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(bookingInfoDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingInfoDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.item.id", is(bookingInfoDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingInfoDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingInfoDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingInfoDto.getStatus().toString())));
    }

    @Test
    void approve() throws Exception {
        bookingInfoDto.setStatus(Status.APPROVED);

        when(bookingService.approve(2L, 1L, true))
                .thenReturn(bookingInfoDto);

        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingInfoDto.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(bookingInfoDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingInfoDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.item.id", is(bookingInfoDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingInfoDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingInfoDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingInfoDto.getStatus().toString())));
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.get(1L, 1L))
                .thenReturn(bookingInfoDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingInfoDto.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(bookingInfoDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingInfoDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.item.id", is(bookingInfoDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingInfoDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingInfoDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingInfoDto.getStatus().toString())));
    }

    @Test
    void getAll() throws Exception {
        when(bookingService.get(1L, "ALL", 0L, 10L))
                .thenReturn(List.of(bookingInfoDto));

        mvc.perform(get("/bookings/")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingInfoDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start",
                        is(bookingInfoDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].end", is(bookingInfoDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].item.id", is(bookingInfoDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingInfoDto.getItem().getName())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingInfoDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingInfoDto.getStatus().toString())));
    }

    @Test
    void getByOwner() throws Exception {
        when(bookingService.getByOwner(1L, "ALL", 0L, 10L))
                .thenReturn(List.of(bookingInfoDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingInfoDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start",
                        is(bookingInfoDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].end", is(bookingInfoDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].item.id", is(bookingInfoDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingInfoDto.getItem().getName())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingInfoDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingInfoDto.getStatus().toString())));
    }
}
