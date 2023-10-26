package ru.practicum.ewm.service;

import ru.practicum.ewm.model.comment.CommentDto;
import ru.practicum.ewm.model.comment.NewCommentDto;
import ru.practicum.ewm.model.comment.UpdateCommentRequest;

import java.util.List;

public interface CommentService {
    CommentDto postComment(Long userId, Long eventId, NewCommentDto commentDto);

    CommentDto updateComment(Long userId, Long eventId, Long commId, UpdateCommentRequest request);

    void deleteComment(Long userId, Long eventId, Long commId);

    void deleteCommentByAdmin(Long eventId, Long commId);

    CommentDto getComment(Long userId, Long eventId, Long commId);

    CommentDto getPublicComment(Long eventId, Long commId);

    List<CommentDto> getAllComments(Long eventId);

    CommentDto updateCommentByAdmin(Long eventId, Long commId, UpdateCommentRequest request);

//    List<CommentDto> getAllComments(Long userId, Long eventId);
}
