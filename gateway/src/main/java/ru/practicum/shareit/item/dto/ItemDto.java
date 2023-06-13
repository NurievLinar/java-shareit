package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {

    private long id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 50)
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;

}
