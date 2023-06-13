package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdOrderByStartDesc(Long userId, PageRequest pageRequest);

    List<Booking> findAllByBooker_IdAndStartIsAfter(Long userId, LocalDateTime start, Sort sort, PageRequest pageRequest);

    List<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(
            Long userId, LocalDateTime start, LocalDateTime end, Sort sort, PageRequest pageRequest);

    List<Booking> findAllByBooker_IdAndEndIsBefore(Long userId, LocalDateTime start, Sort sort, PageRequest pageRequest);

    List<Booking> findAllByBooker_IdAndStatus(Long userId, Status status, PageRequest pageRequest);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long userId, PageRequest pageRequest);

    List<Booking> findAllByItem_Owner_IdAndStartIsAfter(
            Long userId, LocalDateTime start, Sort sort, PageRequest pageRequest);

    List<Booking> findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
            Long userId, LocalDateTime start, LocalDateTime end, Sort sort, PageRequest pageRequest);

    List<Booking> findAllByItem_Owner_IdAndEndIsBefore(Long userId, LocalDateTime start, Sort sort, PageRequest pageRequest);

    List<Booking> findAllByItem_Owner_IdAndStatus(Long userId, Status status, PageRequest pageRequest);

    Optional<Booking> findTop1BookingByItem_IdAndBooker_IdAndEndIsBeforeAndStatusIs(
            Long itemId, Long bookerId, LocalDateTime end, Status status, Sort sort);

    Optional<Booking> findTop1BookingByItem_IdAndStartIsBeforeAndStatusIs(Long itemId, LocalDateTime now, Status approved, Sort sortDesc);

    Optional<Booking> findTop1BookingByItem_IdAndStartIsAfterAndStatusIs(Long itemId, LocalDateTime now, Status approved, Sort sortAsc);

    Collection<Booking> findAllByItem_IdInOrderByStartAsc(Set<Long> itemsIds);

}
