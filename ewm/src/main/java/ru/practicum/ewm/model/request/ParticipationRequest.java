package ru.practicum.ewm.model.request;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "participation_requests",  schema = "public")
public class ParticipationRequest {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "requester_id")
    private Long requester;
    @Column(name = "event_id")
    private Long event;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RequestStatus status = RequestStatus.PENDING;
    @Column(name = "created")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime created = LocalDateTime.now().withNano(0);
}
