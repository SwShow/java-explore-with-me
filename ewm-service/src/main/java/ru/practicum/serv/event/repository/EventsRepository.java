package ru.practicum.serv.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.serv.event.model.Event;

import java.util.List;

@Repository
public interface EventsRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    List<Event> findAllByIdIn(List<Long> eventId);

    Long countByCategoryId(Long catId);
}
