package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.model.comment.CommentDto;
import ru.practicum.ewm.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/comments")
@RequiredArgsConstructor
public class CommentPublicController {

    private final CommentService commentService;

    @GetMapping("/{commId}")
    public ResponseEntity<CommentDto> getComment(
            @PathVariable Long eventId,
            @PathVariable Long commId) {
        return ResponseEntity.ok().body(commentService.getPublicComment(eventId, commId));
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getAllComments(@PathVariable Long eventId) {
        return ResponseEntity.ok().body(commentService.getAllComments(eventId));
    }
}
