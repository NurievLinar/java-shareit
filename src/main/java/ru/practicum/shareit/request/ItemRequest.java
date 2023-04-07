package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
public class ItemRequest {

    private Long id; // уникальный идентификатор запроса;

    private String description; // текст запроса, содержащий описание требуемой вещи;

    private User requestor; // пользователь, создавший запрос;

    private LocalDateTime created; //дата и время создания запроса.

}
