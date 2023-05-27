package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    MockMvc mvc;

    ItemRequestDto itemRequestDtoCreateTest;

    ItemRequestDto itemRequestDtoCreated;

    ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("nameCreate")
                .description("create description")
                .available(true)
                .owner(1L)
                .requestId(1L)
                .build();

        itemRequestDtoCreateTest = ItemRequestDto.builder()
                .description("need smth")
                .build();

        itemRequestDtoCreated = ItemRequestDto.builder()
                .id(1L)
                .description("need smth")
                .requestor(2L)
                .created(LocalDateTime.now())
                .build();
    }

    @AfterEach
    void tearDown() {
        itemRequestDtoCreateTest = null;

        itemRequestDtoCreated = null;

        itemDto = null;
    }

    @Test
    void create() throws Exception {
        when(itemRequestService.create(2L, itemRequestDtoCreateTest))
                .thenReturn(itemRequestDtoCreated);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoCreateTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoCreated.getDescription())))
                .andExpect(jsonPath("$.requestor", is(itemRequestDtoCreated.getRequestor()), Long.class))
                .andExpect(jsonPath("$.created",
                        is(itemRequestDtoCreated.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));
    }

    @Test
    void getOwnRequest() throws Exception {
        itemRequestDtoCreated.setItems(List.of(itemDto));

        when(itemRequestService.getAllUserRequest(2L))
                .thenReturn(List.of(itemRequestDtoCreated));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoCreated.getDescription())))
                .andExpect(jsonPath("$[0].requestor", is(itemRequestDtoCreated.getRequestor()), Long.class))
                .andExpect(jsonPath("$[0].created",
                        is(itemRequestDtoCreated.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].items[0].id",
                        is(itemRequestDtoCreated.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name",
                        is(itemRequestDtoCreated.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[0].description",
                        is(itemRequestDtoCreated.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items[0].available",
                        is(itemRequestDtoCreated.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$[0].items[0].owner",
                        is(itemRequestDtoCreated.getItems().get(0).getOwner()), Long.class))
                .andExpect(jsonPath("$[0].items[0].requestId",
                        is(itemRequestDtoCreated.getItems().get(0).getRequestId()), Long.class));
    }

    @Test
    void getAll() throws Exception {
        itemRequestDtoCreated.setItems(List.of(itemDto));

        when(itemRequestService.getAllRequests(3L, 0L, 10L))
                .thenReturn(List.of(itemRequestDtoCreated));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 3L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoCreated.getDescription())))
                .andExpect(jsonPath("$[0].requestor", is(itemRequestDtoCreated.getRequestor()), Long.class))
                .andExpect(jsonPath("$[0].created",
                        is(itemRequestDtoCreated.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].items[0].id",
                        is(itemRequestDtoCreated.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name",
                        is(itemRequestDtoCreated.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[0].description",
                        is(itemRequestDtoCreated.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items[0].available",
                        is(itemRequestDtoCreated.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$[0].items[0].owner",
                        is(itemRequestDtoCreated.getItems().get(0).getOwner()), Long.class))
                .andExpect(jsonPath("$[0].items[0].requestId",
                        is(itemRequestDtoCreated.getItems().get(0).getRequestId()), Long.class));
    }

    @Test
    void getRequestById() throws Exception {
        itemRequestDtoCreated.setItems(List.of(itemDto));

        when(itemRequestService.getRequestById(3L, 1L))
                .thenReturn(itemRequestDtoCreated);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 3L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoCreated.getDescription())))
                .andExpect(jsonPath("$.requestor", is(itemRequestDtoCreated.getRequestor()), Long.class))
                .andExpect(jsonPath("$.created",
                        is(itemRequestDtoCreated.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.items[0].id",
                        is(itemRequestDtoCreated.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name",
                        is(itemRequestDtoCreated.getItems().get(0).getName())))
                .andExpect(jsonPath("$.items[0].description",
                        is(itemRequestDtoCreated.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$.items[0].available",
                        is(itemRequestDtoCreated.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$.items[0].owner",
                        is(itemRequestDtoCreated.getItems().get(0).getOwner()), Long.class));
    }
}