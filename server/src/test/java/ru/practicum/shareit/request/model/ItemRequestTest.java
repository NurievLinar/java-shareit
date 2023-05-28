package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ItemRequestTest {

    @Test
    void testEquals() {
        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(1L)
                .build();

        ItemRequest itemRequest3 = ItemRequest.builder()
                .id(null)
                .build();

        Assertions.assertTrue(itemRequest1.equals(itemRequest2));
        Assertions.assertTrue(itemRequest1.equals(itemRequest1));
        Assertions.assertFalse(itemRequest1.equals(1L));
        Assertions.assertFalse(itemRequest1.equals(itemRequest3));
        Assertions.assertFalse(itemRequest3.equals(itemRequest1));
    }
}
