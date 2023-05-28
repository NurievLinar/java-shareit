package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.exception.PaginationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {

    @Autowired
    private final ItemRequestServiceImpl itemRequestService;

    @MockBean
    private final ItemRequestRepository itemRequestRepository;

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final ItemRepository itemRepository;

    @Test
    void create() {
        User requstor = User.builder()
                .id(2L)
                .name("name")
                .email("user@email.com")
                .build();

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(requstor));

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("description")
                .build();

        LocalDateTime now = LocalDateTime.now();

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(requstor)
                .created(now)
                .build();

        when(itemRequestRepository.save(any()))
                .thenReturn(request);

        ItemRequestDto itemRequestDtoCreated = itemRequestService.create(2L, requestDto);
        assertThat(itemRequestDtoCreated, is(notNullValue()));
    }

    @Test
    void throwUserNotFoundException() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("description")
                .build();

        UserNotFoundException invalidUserIdException;

        invalidUserIdException = Assertions.assertThrows(UserNotFoundException.class,
                () -> itemRequestService.create(2L, requestDto));
        assertThat(invalidUserIdException.getMessage(), is("Пользователь не найден"));

        invalidUserIdException = Assertions.assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getAllUserRequest(2L));
        assertThat(invalidUserIdException.getMessage(), is("Пользователь не найден"));

        invalidUserIdException = Assertions.assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getAllRequests(2L, 0L, 10L));
        assertThat(invalidUserIdException.getMessage(), is("Пользователь не найден"));

        invalidUserIdException = Assertions.assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getRequestById(2L, 1L));
        assertThat(invalidUserIdException.getMessage(), is("Пользователь не найден"));
    }

    @Test
    void getRequestListRelatedToRequestor() {
        User requstor = User.builder()
                .id(2L)
                .name("name2")
                .email("user2@email.com")
                .build();

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(requstor));

        when(itemRequestRepository.findAllByRequestor_IdOrderByCreatedDesc(2L))
                .thenReturn(new ArrayList<>());

        List<ItemRequestDto> itemRequestDtos = itemRequestService.getAllUserRequest(2L);

        assertTrue(itemRequestDtos.isEmpty());

        LocalDateTime requestCreationDate = LocalDateTime.now();

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(requstor)
                .created(requestCreationDate)
                .build();

        List<ItemRequest> itemRequests = List.of(request);

        when(itemRequestRepository.findAllByRequestor_IdOrderByCreatedDesc(2L))
                .thenReturn(itemRequests);

        User owner = User.builder()
                .id(1L)
                .name("name1")
                .email("user1@email.com")
                .build();

        List<Item> items = Collections.emptyList();

        when(itemRepository.findAllByRequest_IdIn(List.of(1L)))
                .thenReturn(items);

        itemRequestDtos = itemRequestService.getAllUserRequest(2L);

        assertTrue(itemRequestDtos.get(0).getItems().isEmpty());

        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .request(request)
                .build();

        items = List.of(item);

        when(itemRepository.findAllByRequest_IdIn(List.of(1L)))
                .thenReturn(items);

        itemRequestDtos = itemRequestService.getAllUserRequest(2L);

        assertThat(itemRequestDtos, is(notNullValue()));
    }

    @Test
    void getRequestListOfOtherRequestors() {
        User owner = User.builder()
                .id(1L)
                .name("name1")
                .email("user1@email.com")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        LocalDateTime requestCreationDate = LocalDateTime.now();

        User requestor = User.builder()
                .id(2L)
                .name("name2")
                .email("user2@email.com")
                .build();

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(requestor)
                .created(requestCreationDate)
                .build();

        List<ItemRequest> itemRequests = new ArrayList<>();

        when(itemRequestRepository.findAllByRequestor_IdIsNot(any(), any()))
                .thenReturn(itemRequests);
        List<ItemRequestDto> itemRequestDtos = itemRequestService.getAllRequests(1L, 0L, 10L);
        assertTrue(itemRequestDtos.isEmpty());

        itemRequests = List.of(request);
        when(itemRequestRepository.findAllByRequestor_IdIsNot(any(), any()))
                .thenReturn(itemRequests);

        List<Item> items = Collections.emptyList();
        when(itemRepository.findAllByRequest_IdIn(List.of(1L)))
                .thenReturn(items);

        itemRequestDtos = itemRequestService.getAllRequests(1L, 0L, 10L);
        assertTrue(itemRequestDtos.get(0).getItems().isEmpty());

        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .request(request)
                .build();
        items = List.of(item);

        when(itemRepository.findAllByRequest_IdIn(List.of(1L)))
                .thenReturn(items);

        itemRequestDtos = itemRequestService.getAllRequests(1L, 0L, 10L);
        assertThat(itemRequestDtos, is(notNullValue()));
    }

    @Test
    void throwPaginationException() {
        User owner = User.builder()
                .id(1L)
                .name("name1")
                .email("user1@email.com")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        PaginationException invalidPageParamsException;

        invalidPageParamsException = Assertions.assertThrows(PaginationException.class,
                () -> itemRequestService.getAllRequests(1L, -1L, 10L));
        assertThat(invalidPageParamsException.getMessage(), is("Ошибка пагинации"));

        invalidPageParamsException = Assertions.assertThrows(PaginationException.class,
                () -> itemRequestService.getAllRequests(1L, 0L, 0L));
        assertThat(invalidPageParamsException.getMessage(), is("Ошибка пагинации"));
    }

    @Test
    void getRequestByIdByAnyUser() {
        User owner = User.builder()
                .id(1L)
                .name("name1")
                .email("user1@email.com")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        LocalDateTime requestCreationDate = LocalDateTime.now();

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(owner)
                .created(requestCreationDate)
                .build();

        when(itemRequestRepository.findItemRequestById(request.getId()))
                .thenReturn(Optional.of(request));

        List<Item> items = Collections.emptyList();
        when(itemRepository.findAllByRequest_Id(1L))
                .thenReturn(items);

        ItemRequestDto itemRequestDto = itemRequestService.getRequestById(1L, 1L);
        assertTrue(itemRequestDto.getItems().isEmpty());

        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .request(request)
                .build();
        items = List.of(item);

        when(itemRepository.findAllByRequest_Id(1L))
                .thenReturn(items);

        itemRequestDto = itemRequestService.getRequestById(1L, 1L);

        assertThat(itemRequestDto, is(notNullValue()));
    }

    @Test
    void throwItemRequestNotFoundException() {
        User owner = User.builder()
                .id(1L)
                .name("name1")
                .email("user1@email.com")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        when(itemRequestRepository.findItemRequestById(any()))
                .thenReturn(Optional.empty());

        ItemRequestNotFoundException invalidItemRequestIdException = Assertions.assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getRequestById(1L, 1L));
        assertThat(invalidItemRequestIdException.getMessage(), is("Запрос не найден"));
    }
}
