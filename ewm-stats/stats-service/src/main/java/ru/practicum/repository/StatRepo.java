package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Hit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepo extends JpaRepository<Hit, Long> {

    @Query(value = "select new ru.practicum.model.ViewStats(hit.app, hit.uri, count(distinct hit.ip)) " +
            "from Hit hit where hit.timestamp between ?1 and ?2 " +
            "and hit.uri in ?3 " +
            "group by hit.app, hit.uri " +
            "order by count(distinct hit.ip) desc")
    List<ViewStats> findByUriAndUniqueIp(LocalDateTime startDate, LocalDateTime endDate, List<String> uris);

    @Query(value = "select new ru.practicum.model.ViewStats(hit.app, hit.uri, count(hit.ip)) " +
            "from Hit hit where hit.timestamp between ?1 and ?2 " +
            "and hit.uri in ?3 " +
            "group by hit.app, hit.uri " +
            "order by count(hit.ip) desc")
    List<ViewStats> findByUri(LocalDateTime startDate, LocalDateTime endDate, List<String> uris);

}
