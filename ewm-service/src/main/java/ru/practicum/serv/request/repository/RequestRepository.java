package ru.practicum.serv.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.serv.request.model.Request;
import ru.practicum.serv.statuses.RequestStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByIdInAndStatus(List<Long> id, RequestStatus status);

    List<Request> findAllByRequesterId(Long requesterId);

    Optional<Request> findByRequesterIdAndEventId(Long userId, Long eventId);
}
