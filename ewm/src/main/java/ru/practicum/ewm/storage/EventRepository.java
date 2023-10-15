package ru.practicum.ewm.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {


    Page<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    Event findByIdAndInitiatorIdAndStateIn(Long eventId, Long initiatorId, List<EventState> states);

    @Query("select e " +
            "from Event e " +
            "where initiator_id IN (?1) " +
            "and state IN (?2) " +
            "and category_id IN (?3) " +
            "and event_date BETWEEN ?4 AND ?5")
    Page<Event> findAllByInitiatorIdAndStateAndCategoryAndEventDate(
            Set<Long> users,
            Set<EventState> states,
            Set<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Pageable pageable);
}
