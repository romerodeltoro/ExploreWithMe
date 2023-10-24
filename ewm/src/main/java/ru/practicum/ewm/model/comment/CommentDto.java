package ru.practicum.ewm.model.comment;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Long id;
    private String text;
    private String authorName;
    private Boolean participant;
    private LocalDateTime created;

}
