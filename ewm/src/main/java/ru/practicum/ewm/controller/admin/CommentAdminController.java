package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.comment.CommentDto;
import ru.practicum.ewm.model.comment.UpdateCommentRequest;
import ru.practicum.ewm.service.CommentService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/events/{eventId}/comments")
public class CommentAdminController {

    private final CommentService commentService;

    @DeleteMapping("/{commId}")
    public ResponseEntity<Void> deleteCommentByAdmin(
            @PathVariable Long eventId,
            @PathVariable Long commId) {
        commentService.deleteCommentByAdmin(eventId, commId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{commId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long eventId,
            @PathVariable Long commId,
            @RequestBody @Valid UpdateCommentRequest request) {
        return ResponseEntity.ok().body(commentService.updateCommentByAdmin(eventId, commId, request));
    }
}
