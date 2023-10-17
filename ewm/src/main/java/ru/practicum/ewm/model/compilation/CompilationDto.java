package ru.practicum.ewm.model.compilation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.model.event.Event;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {

    private Long id;
    private Boolean pinned;
    private String title;
    private List<Event> events;
}
