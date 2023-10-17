package ru.practicum.ewm.model.request;

import lombok.*;

@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    private Long id;
    private Long requester;
    private Long event;
    private RequestStatus status;
    private String created;
}
