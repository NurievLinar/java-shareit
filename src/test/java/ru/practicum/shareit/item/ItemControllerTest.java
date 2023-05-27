package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDtoCreateTest;

    private ItemDto itemDtoCreated;

    private ItemDto itemDtoUpdateTest;

    private ItemDto itemDtoUpdated;

    private CommentDto commentDtoCreateTest;

    private CommentDto commentDtoCreated;

    @BeforeEach
    void setUp() {
        itemDtoCreateTest = ItemDto.builder()
                .name("nameCreate")
                .description("create description")
                .available(true)
                .owner(1L)
                .build();

        itemDtoCreated = ItemDto.builder()
                .id(1L)
                .name("nameCreate")
                .description("create description")
                .available(true)
                .owner(1L)
                .build();

        itemDtoUpdateTest = ItemDto.builder()
                .description("update description")
                .build();

        itemDtoUpdated = ItemDto.builder()
                .id(1L)
                .name("nameCreate")
                .description("update description")
                .available(true)
                .owner(1L)
                .build();

        commentDtoCreateTest = CommentDto.builder()
                .text("comment")
                .build();

        commentDtoCreated = CommentDto.builder()
                .id(1L)
                .text("comment")
                .authorName("nameCreate")
                .created(LocalDateTime.now())
                .build();
    }

    @AfterEach
    void tearDown() {
        itemDtoCreateTest = null;
        itemDtoCreated = null;
        itemDtoUpdateTest = null;
        itemDtoUpdated = null;
    }

    @Test
    void addNewItemDto() throws Exception {
        when(itemService.addNewItemDto(itemDtoCreateTest, 1L))
                .thenReturn(itemDtoCreated);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoCreateTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoCreated.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoCreated.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoCreated.getAvailable())))
                .andExpect(jsonPath("$.owner", is(itemDtoCreated.getOwner()), Long.class));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(1L, 1L, itemDtoUpdateTest))
                .thenReturn(itemDtoUpdated);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDtoUpdateTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoUpdated.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoUpdated.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoUpdated.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoUpdated.getAvailable())))
                .andExpect(jsonPath("$.owner", is(itemDtoUpdated.getOwner()), Long.class));
    }

    @Test
    void getItemDtoById() throws Exception {
        when(itemService.getItemDtoById(1L, 1L))
                .thenReturn(itemDtoUpdated);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoUpdated.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoUpdated.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoUpdated.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoUpdated.getAvailable())))
                .andExpect(jsonPath("$.owner", is(itemDtoUpdated.getOwner()), Long.class));
    }

    @Test
    void getOwnerItems() throws Exception {
        when(itemService.getOwnerItems(1L, 0L, 10L))
                .thenReturn(List.of(itemDtoUpdated));

        mvc.perform(get("/items/")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoUpdated.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoUpdated.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoUpdated.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoUpdated.getAvailable())))
                .andExpect(jsonPath("$[0].owner", is(itemDtoUpdated.getOwner()), Long.class));
    }

    @Test
    void searchItems() throws Exception {
        when(itemService.searchItems(1L, "update", 0L, 10L))
                .thenReturn(List.of(itemDtoUpdated));

        mvc.perform(get("/items/search?text=update")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoUpdated.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoUpdated.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoUpdated.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoUpdated.getAvailable())))
                .andExpect(jsonPath("$[0].owner", is(itemDtoUpdated.getOwner()), Long.class));
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(1L, 1L, commentDtoCreateTest))
                .thenReturn(commentDtoCreated);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDtoCreateTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDtoCreated.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDtoCreated.getAuthorName())))
                .andExpect(jsonPath("$.created",
                        is(commentDtoCreated.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));
    }
}