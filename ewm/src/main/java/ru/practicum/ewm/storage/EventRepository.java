package ru.practicum.ewm.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.EventState;

import java.util.List;
import java.util.Optional;


@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {


    Page<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    Event findByIdAndInitiatorIdAndStateIn(Long eventId, Long initiatorId, List<EventState> states);

    Optional<Event> findByIdAndState(Long eventId, EventState state);

    Event findFirstByCategoryId(Long categoryId);

    Optional<List<Event>> findAllByIdIn(List<Long> events);
}
