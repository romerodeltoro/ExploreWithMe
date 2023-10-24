package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.comment.CommentDto;
import ru.practicum.ewm.model.comment.NewCommentDto;
import ru.practicum.ewm.service.CommentService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users/{userId}/events/{eventId}/comments")
public class CommentsPrivateController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> postComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid NewCommentDto commentDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.postComment(userId, eventId, commentDto));
    }

    @PatchMapping("/{commId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long commId,
            @RequestBody @Valid NewCommentDto commentDto) {
        return ResponseEntity.ok().body(commentService.updateComment(userId, eventId, commId, commentDto));
    }

    @DeleteMapping("/{commId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long commId) {
        commentService.deleteComment(userId, eventId, commId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
