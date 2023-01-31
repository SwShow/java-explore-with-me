package ru.practicum.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Hit;
import ru.practicum.model.ViewStats;


import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<Hit, Long> {
    @Query("SELECT NEW ru.practicum.model.ViewStats(hit.app, hit.uri, COUNT(DISTINCT hit.uri)) " +
            "FROM Hit hit WHERE " +
            "hit.timestamp >= :start " +
            "AND hit.timestamp <= :end " +
            "AND hit.uri IN :uris " +
            "GROUP BY hit.app, hit.uri ORDER BY COUNT(hit.id) DESC")
    List<ViewStats> getDistinctStats(LocalDateTime start, LocalDateTime end, String[] uris);

    @Query("SELECT  NEW ru.practicum.model.ViewStats(hit.app, hit.uri, COUNT(hit.uri)) " +
            "FROM Hit hit WHERE " +
            "hit.timestamp >= :start " +
            "AND hit.timestamp <= :end " +
            "AND hit.uri IN :uris " +
            "GROUP BY hit.app, hit.uri ORDER BY COUNT(hit.id) DESC")
    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, String[] uris);
}
