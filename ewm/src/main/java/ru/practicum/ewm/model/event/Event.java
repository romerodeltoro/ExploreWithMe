package ru.practicum.ewm.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.model.compilation.Compilation;
import ru.practicum.ewm.model.location.Location;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "events", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "annotation", nullable = false)
    private String annotation;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(name = "created_on")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn = LocalDateTime.now();
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "event_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;
    @Column(name = "paid", nullable = false)
    private Boolean paid;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "confirmed_requests")
    private Integer confirmedRequests = 0;
    @Column(name = "published_on")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private EventState state = EventState.PENDING;
    @Enumerated(EnumType.STRING)
    @Column(name = "state_action")
    private StateAction stateAction = StateAction.SEND_TO_REVIEW;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "views")
    private Integer views = 0;
}

