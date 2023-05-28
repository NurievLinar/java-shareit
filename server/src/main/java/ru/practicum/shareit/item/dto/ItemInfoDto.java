package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemInfoDto {
    private final Long id;
    private final String name;
}
