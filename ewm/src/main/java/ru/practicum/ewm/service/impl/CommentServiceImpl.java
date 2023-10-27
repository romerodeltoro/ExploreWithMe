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
import ru.practicum.ewm.model.comment.UpdateCommentRequest;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.service.CommentService;
import ru.practicum.ewm.storage.CommentRepository;
import ru.practicum.ewm.storage.EventRepository;
import ru.practicum.ewm.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;


    @Override
    @Transactional
    public CommentDto postComment(Long userId, Long eventId, NewCommentDto newDto) {
        User user = ifUserExistReturn(userId);
        Event event = ifEventExistReturn(eventId);
        Comment comment = commentRepository.save(CommentMapper.INSTANCE.toComment(newDto));
        comment.setAuthor(user);
        comment.setEvent(event);
        CommentDto commentDto = CommentMapper.INSTANCE.toCommentDto(comment);

        log.info("Пользователем с id={} оставлен комментарий к событию с id={}", userId, eventId);
        return commentDto;
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long eventId, Long commId, UpdateCommentRequest request) {
        ifUserExistReturn(userId);
        ifEventExistReturn(eventId);
        Comment comment = ifCommentExistReturn(commId);

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("You have no access rights to content");
        }

        comment.setText(request.getText());
        CommentDto commentDto = CommentMapper.INSTANCE.toCommentDto(comment);

        log.info("Комментарий с id={} обновлен пользователем", commId);
        return commentDto;
    }

    @Override
    @Transactional
    public CommentDto updateCommentByAdmin(Long eventId, Long commId, UpdateCommentRequest request) {
        ifEventExistReturn(eventId);
        Comment comment = ifCommentExistReturn(commId);
        comment.setText(request.getText());
        CommentDto commentDto = CommentMapper.INSTANCE.toCommentDto(comment);

        log.info("Комментарий с id={} обновлен админом", commId);
        return commentDto;
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long eventId, Long commId) {
        Comment comment = ifCommentExistReturn(commId);
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("You have no access rights to content");
        }
        commentRepository.deleteById(commId);
        log.info("Комментарий с id='{}' - удален", commId);
    }

    @Override
    public CommentDto getComment(Long userId, Long eventId, Long commId) {
        ifUserExistReturn(userId);
        ifEventExistReturn(eventId);
        CommentDto comment = CommentMapper.INSTANCE.toCommentDto(ifCommentExistReturn(commId));
        log.info("Пользователь с id={} получил комментарий с id={}", userId, commId);

        return comment;
    }

    @Override
    public List<CommentDto> getAllComments(Long eventId) {
        ifEventExistReturn(eventId);
        List<CommentDto> commentDtos = commentRepository.findAllByEventId(eventId).stream()
                .map(CommentMapper.INSTANCE::toCommentDto)
                .collect(Collectors.toList());
        log.info("К событию с id={} получены все комментарии", eventId);

        return commentDtos;
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long eventId, Long commId) {
        ifCommentExistReturn(commId);
        commentRepository.deleteById(commId);
        log.info("Комментарий с id='{}' - удален админом", commId);
    }

    @Override
    public CommentDto getPublicComment(Long eventId, Long commId) {
        ifEventExistReturn(eventId);
        CommentDto commentDto = CommentMapper.INSTANCE.toCommentDto(ifCommentExistReturn(commId));
        log.info("К событию с id={} получен комментарий с id={}", eventId, commId);

        return commentDto;
    }

//    @Override
//    public List<CommentDto> getAllcomments(Long eventId) {
//        ifEventExistReturn(eventId);
//        List<CommentDto> commentDtos = commentRepository.findAllByEventId(eventId).stream()
//                .map(CommentMapper.INSTANCE::toCommentDto)
//                .collect(Collectors.toList());
//        log.info("К событию с id={} получены все комментарии", eventId);
//
//        return commentDtos;
//    }

    private Comment ifCommentExistReturn(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("Комментария с id %d нет в базе", id)));
    }

    private User ifUserExistReturn(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("Пользователя с id %d нет в базе", id)));
    }

    private Event ifEventExistReturn(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("Вещи с id %d нет в базе", id)));
    }
}
