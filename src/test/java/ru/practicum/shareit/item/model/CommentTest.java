package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CommentTest {

    @Test
    void testEquals() {
        Comment comment1 = Comment.builder()
                .id(1L)
                .build();

        Comment comment2 = Comment.builder()
                .id(1L)
                .build();

        Comment comment3 = Comment.builder()
                .id(null)
                .build();

        Assertions.assertTrue(comment1.equals(comment2));
        Assertions.assertTrue(comment1.equals(comment1));
        Assertions.assertFalse(comment1.equals(1L));
        Assertions.assertFalse(comment1.equals(comment3));
        Assertions.assertFalse(comment3.equals(comment1));
    }
}