package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.comment.Comment;
import ru.practicum.ewm.model.comment.CommentDto;
import ru.practicum.ewm.model.comment.NewCommentDto;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.request.ParticipationRequest;
import ru.practicum.ewm.model.request.RequestStatus;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.service.CommentService;
import ru.practicum.ewm.storage.CommentRepository;
import ru.practicum.ewm.storage.EventRepository;
import ru.practicum.ewm.storage.RequestRepository;
import ru.practicum.ewm.storage.UserRepository;

import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    @Transactional
    public CommentDto postComment(Long userId, Long eventId, NewCommentDto newDto) {
        User user = ifUserExistReturnUser(userId);
        Event event = ifEventExistReturnItem(eventId);
        ParticipationRequest request = requestRepository.findByRequesterAndEvent(userId, eventId);
        Comment comment = commentRepository.save(CommentMapper.INSTANCE.toComment(newDto));
        comment.setAuthor(user);
        comment.setEvent(event);
        CommentDto commentDto = CommentMapper.INSTANCE.toCommentDto(comment);
        commentDto.setParticipant(request.getStatus().equals(RequestStatus.CONFIRMED));

        log.info("Пользователем с id={} оставлен комментарий к событию с id={}", userId, eventId);
        return commentDto;
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long eventId, Long commId, NewCommentDto newDto) {
        ifUserExistReturnUser(userId);
        ifEventExistReturnItem(eventId);
        Comment comment = ifCommentExistReturnUser(commId);

        if(comment.getAuthor().getId() != userId) {
            throw new ForbiddenException("У вас нет прав доступа к содержимому");
        }

        ParticipationRequest request = requestRepository.findByRequesterAndEvent(userId, eventId);
        comment.setText(newDto.getText());
        CommentDto commentDto = CommentMapper.INSTANCE.toCommentDto(comment);
        commentDto.setParticipant(request.getStatus().equals(RequestStatus.CONFIRMED));

        log.info("Комментарий с id={} обновлен пользователем", commId);
        return commentDto;
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long eventId, Long commId) {
        Comment comment = ifCommentExistReturnUser(commId);
        if(comment.getAuthor().getId() != userId) {
            throw new ForbiddenException("У вас нет прав доступа к содержимому");
        }
        commentRepository.deleteById(commId);
        log.info("Комментарий с id='{}' - удален", commId);
    }

    private Comment ifCommentExistReturnUser(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("Комментария с id %d нет в базе", id)));
    }

    private User ifUserExistReturnUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("Пользователя с id %d нет в базе", id)));
    }


    private Event ifEventExistReturnItem(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("Вещи с id %d нет в базе", id)));
    }
}
