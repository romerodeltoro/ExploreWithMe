package ru.practicum.ewm.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.request.ParticipationRequest;
import ru.practicum.ewm.model.request.RequestStatus;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    ParticipationRequest findByEventAndRequester(Long event, Long requester);

    List<ParticipationRequest> findAllByRequester(Long requester);
    List<ParticipationRequest> findAllByEvent(Long event);
    @Query("select r " +
            "from ParticipationRequest r " +
            "where id IN ?1 " +
            "and status = 'PENDING'")
    List<ParticipationRequest> findAllByIds(List<Long> ids);
    List<ParticipationRequest> findAllByStatus(RequestStatus status);

}
