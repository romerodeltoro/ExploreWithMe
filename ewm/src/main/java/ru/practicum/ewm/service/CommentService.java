package ru.practicum.ewm.service;

import ru.practicum.ewm.model.comment.CommentDto;
import ru.practicum.ewm.model.comment.NewCommentDto;

public interface CommentService {
    CommentDto postComment(Long userId, Long eventId, NewCommentDto commentDto);

    CommentDto updateComment(Long userId, Long eventId, Long commId, NewCommentDto commentDto);

    void deleteComment(Long userId, Long eventId, Long commId);
}
