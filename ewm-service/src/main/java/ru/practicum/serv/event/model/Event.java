package ru.practicum.serv.event.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.serv.category.model.Category;
import ru.practicum.serv.request.model.Request;
import ru.practicum.serv.statuses.State;
import ru.practicum.serv.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 500)
    private String annotation;  // краткое описание
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Request> requests;
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;  // дата создания события
    @Column(name = "description", length = 1000)
    private String description;  // полное описание
    @Column(name = "event_date")
    private LocalDateTime eventDate;  // дата намеченного события
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "lat", column = @Column(name = "lat")),
            @AttributeOverride(name = "lon", column = @Column(name = "lon"))
    })
    private Location location;
    @Column(nullable = false)
    private Boolean paid;  // нужно ли оплачивать событие
    @Column(name = "participant_limit")
    private Integer participantLimit;  // лимит количества участников

    @Column(name = "published_on")
    private LocalDateTime publishedOn;  // дата и время публикации
    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;  // нужна ли пре-модерация заявок на участие
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;  // статус события
    @Column(nullable = false)
    private String title; // заголовок
    @Column(nullable = false)
    private Long views;  // количество просмотров

}
