package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwner_Id(Long userId, PageRequest pageRequest);

    @Query("from Item as it " +
            "where " +
            "it.available = true " +
            "and " +
            "(upper(it.name) like upper(concat('%',?1,'%')) or upper(it.description) like upper(concat('%',?1,'%')))")
    List<Item> searchAvailableByText(String text, PageRequest pageRequest);

    List<Item> findAllByRequest_IdIn(List<Long> requestIdList);

    List<Item> findAllByRequest_Id(Long requestId);
}
