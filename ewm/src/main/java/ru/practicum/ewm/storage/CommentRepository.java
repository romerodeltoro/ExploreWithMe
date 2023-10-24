package ru.practicum.ewm.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.comment.Comment;
import ru.practicum.ewm.model.comment.CommentDto;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEventId(Long eventId);

//    @Query(value = "select c.id, c.text, u.name, pr.status, c.created " +
//            "from comments c " +
//            "join users u on c.author_id = u.id " +
//            "join participation_requests pr on c.author_id = pr.requester_id " +
//            "where c.event_id = :eventId",
//            nativeQuery = true)
//    List<CommentDto> findAllByEventIdJoinRequests(@Param("eventId") Long eventId);
//
//    @Query(value = "select c.id, c.text, u.name as authorName, pr.status, c.created " +
//            "from comments c " +
//            "join users u on c.author_id = u.id " +
//            "join participation_requests pr on c.author_id = pr.requester_id " +
//            "where c.event_id = :eventId",
//            nativeQuery = true)
//    Object[] findAllLikeObject(@Param("eventId") Long eventId);
}
