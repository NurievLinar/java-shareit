package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // уникальный идентификатор запроса;

    @Column(name = "description")
    private String description; // текст запроса, содержащий описание требуемой вещи;

    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requestor; // пользователь, создавший запрос;

    @Column(name = "create_date")
    private LocalDateTime created; //дата и время создания запроса.
}