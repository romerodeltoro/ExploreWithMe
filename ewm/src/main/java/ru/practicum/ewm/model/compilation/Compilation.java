package ru.practicum.ewm.model.compilation;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "compilations", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class Compilation {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "pinned")
    private Boolean pinned = false;
    @Column(name = "title")
    private String title;
    @Type(type = "jsonb")
    @Column(name = "events", columnDefinition = "jsonb")
    private List<Long> events;

}
