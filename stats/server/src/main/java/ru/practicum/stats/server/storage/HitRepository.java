package ru.practicum.stats.server.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.server.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("select h.app, h.uri, COUNT(h.ip) " +
            "from Hit h " +
            "where h.timestamp between ?1 and ?2 " +
            "group by h.app, h.uri " +
            "order by COUNT(h.ip) desc")
    List<Object[]> findAllByTimestampWhereUrisIsNull(LocalDateTime start, LocalDateTime end);

    @Query("select h.app, h.uri, COUNT(DISTINCT h.ip) " +
            "from Hit h " +
            "where h.timestamp between ?1 and ?2 " +
            "group by h.app, h.uri " +
            "order by COUNT(h.ip) desc")
    List<Object[]> findAllByTimestampWhereUrisIsNullAndUniqueTrue(LocalDateTime start, LocalDateTime end);

    @Query("select h.app, h.uri, COUNT(DISTINCT h.ip) " +
            "from Hit h " +
            "where h.timestamp between ?1 and ?2 " +
            "and h.uri IN (?3) " +
            "group by h.app, h.uri " +
            "order by COUNT(DISTINCT h.ip) desc")
    List<Object[]> findAllByTimestampWhereUrisAndUniqueTrue(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select h.app, h.uri, COUNT(h.ip) " +
            "from Hit h " +
            "where h.timestamp between ?1 and ?2 " +
            "and h.uri IN (?3) " +
            "group by h.app, h.uri " +
            "order by COUNT(h.ip) desc")
    List<Object[]> findAllByTimestampWhereUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}
