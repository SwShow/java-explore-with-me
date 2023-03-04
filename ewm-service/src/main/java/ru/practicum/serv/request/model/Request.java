package ru.practicum.serv.request.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.serv.event.model.Event;
import ru.practicum.serv.statuses.RequestStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreationTimestamp
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @JsonBackReference
    private Event event;
    @JoinColumn(name = "requester_id", unique = true)
    private Long requesterId;
    @Enumerated(EnumType.STRING)
    @Column
    private RequestStatus status;
}
