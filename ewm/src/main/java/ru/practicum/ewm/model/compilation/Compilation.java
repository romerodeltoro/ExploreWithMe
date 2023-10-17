package ru.practicum.ewm.model.compilation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.model.event.Event;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Entity
@Table(name = "compilations", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
public class Compilation {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "pinned")
    private Boolean pinned;
    @Column(name = "title")
    private String title;
    @OneToMany(mappedBy = "event")
    private List<Event> events = new ArrayList<>();

}
