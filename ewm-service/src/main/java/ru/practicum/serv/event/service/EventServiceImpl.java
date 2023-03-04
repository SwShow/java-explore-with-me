package ru.practicum.serv.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatistClient;
import ru.practicum.serv.category.model.Category;
import ru.practicum.serv.category.repository.CategoryRepository;
import ru.practicum.serv.event.dto.EventDto;
import ru.practicum.serv.event.dto.NewEventDto;
import ru.practicum.serv.event.dto.UpdateEventAdminRequest;
import ru.practicum.serv.event.dto.UpdateEventUserRequest;
import ru.practicum.serv.event.mapper.EventMapper;
import ru.practicum.serv.event.model.Event;
import ru.practicum.serv.event.model.QEvent;
import ru.practicum.serv.event.repository.EventsRepository;
import ru.practicum.serv.exception.ConflictException;
import ru.practicum.serv.exception.NotFoundException;
import ru.practicum.serv.exception.ValidationException;
import ru.practicum.serv.statuses.SortEv;
import ru.practicum.serv.statuses.State;
import ru.practicum.serv.user.model.User;
import ru.practicum.serv.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.serv.statuses.State.*;
import static ru.practicum.serv.statuses.StateAction.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventsRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatistClient client;

    @Transactional
    @Override
    public Collection<EventDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart,
                                                 LocalDateTime rangeEnd, Integer from, Integer size) {
        log.info("получен запрос от администратора на получение событий в категориях {}", categories);

        Pageable pageable = PageRequest.of(from, size);
        QEvent qEvent = QEvent.event;
        BooleanExpression expression = qEvent.id.isNotNull();
        if (users != null && users.size() > 0) {
            expression = expression.and(qEvent.initiator.id.in(users));
        }
        if (states != null && states.size() > 0) {
            expression = expression.and(qEvent.state.in(states.stream()
                    .map(State::valueOf)
                    .collect(Collectors.toUnmodifiableList())));
        }
        if (categories != null && categories.size() > 0) {
            expression = expression.and(qEvent.category.id.in(categories));
        }
        if (rangeStart != null) {
            expression = expression.and(qEvent.eventDate.goe(rangeStart));
        }
        if (rangeEnd != null) {
            expression = expression.and(qEvent.eventDate.loe(rangeEnd));
        }

        Collection<Event> events = eventRepository.findAll(expression, pageable).getContent();
        return events.stream().map(EventMapper.INSTANCE::toEventDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Collection<EventDto> getAllEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd, Boolean onlyAvailable, SortEv sort, Integer from, Integer size,
                                             HttpServletRequest httpRequest) {
        log.info("Получен запрос на получение событий с возможностью фильтрации");
        log.info("client ip: {}", httpRequest.getRemoteAddr());
        log.info("endpoint path: {}", httpRequest.getRequestURI());
        Pageable pageable = PageRequest.of(from, size);
        QEvent qEvent = QEvent.event;
        BooleanExpression expression = qEvent.state.eq(State.PUBLISHED);
        if (text != null) {
            expression = expression.and(qEvent.annotation.containsIgnoreCase(text).or(qEvent.description.containsIgnoreCase(text)));
        }
        if (paid != null) {
            expression = expression.and(qEvent.paid.eq(paid));
        }
        if (categories != null && categories.size() > 0) {
            expression = expression.and(qEvent.category.id.in(categories));
        }
        if (rangeStart != null) {
            expression = expression.and(qEvent.eventDate.goe(rangeStart));
        }
        if (rangeEnd != null) {
            expression = expression.and(qEvent.eventDate.loe(rangeEnd));
        }
        Collection<Event> events = eventRepository.findAll(expression, pageable).getContent();
        client.addHit(httpRequest);
        return events.stream().map(EventMapper.INSTANCE::toEventDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventDto pathEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("получен запрос на редактирование данных события с id {} админом", eventId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено"));
        if (updateEventAdminRequest.getEventDate() != null
                && updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Время редактирования события закончилось");
        }
        if (updateEventAdminRequest.getStateAction().equals(PUBLISH_EVENT)) {

            if (event.getState().equals(PUBLISHED) || event.getState().equals(CANCELED)) {
                throw new ConflictException("Невозможно опубликовать это событие");
            }
            event.setState(PUBLISHED);
            log.info("Установлен статус PUBLISHED");
        }
        if (updateEventAdminRequest.getStateAction().equals(REJECT_EVENT)) {
            if (event.getState().equals(PUBLISHED)) {
                throw new ConflictException("Нельзя отменить опубликованное событие");
            }
            event.setState(CANCELED);
            log.info("Установлен статус " + event.getState());
        }
        Optional.ofNullable(updateEventAdminRequest.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(updateEventAdminRequest.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateEventAdminRequest.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateEventAdminRequest.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateEventAdminRequest.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEventAdminRequest.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEventAdminRequest.getRequestModeration()).ifPresent(event::setRequestModeration);

        if (updateEventAdminRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventAdminRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория не найдена"));
            event.setCategory(category);
        }
        return EventMapper.INSTANCE.toEventDto(eventRepository.save(event));
    }

    @Transactional
    @Override
    public EventDto getFullInfoEvent(Long id, HttpServletRequest httpRequest) {
        log.info("Получен запрос на получение инфо о событии с ид-ом {}", id);
        log.info("client ip: {}", httpRequest.getRemoteAddr());
        log.info("endpoint path: {}", httpRequest.getRequestURI());
        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Событие не найдено"));
        client.addHit(httpRequest);
        return EventMapper.INSTANCE.toEventDto(event);
    }

    @Override
    public List<EventDto> getEventOwner(Long userId, int from, int size) {
        log.info("Получение событий текущего пользователя {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("пользователь с ид-ом:" + userId + "не найден");
        }
        Pageable pageable = PageRequest.of(from, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        return events.stream()
                .map(EventMapper.INSTANCE::toEventDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventDto addEventOwner(Long userId, NewEventDto newEventDto) {
        log.info("Добавление нового события пользователем {}", userId);
        if (newEventDto == null) {
            throw new ValidationException("Заголовок не может быть пустым");
        }
        if (newEventDto.getEventDate() != null
                && newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("время события не соответствует условиям");
        }
        User initiator = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не авторизован"));
        log.info("initiator:" + initiator);
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория не найдена"));
        log.info("category:" + category);

        Event event = new Event(0L, newEventDto.getAnnotation(), category, new ArrayList<>(), LocalDateTime.now(),
                newEventDto.getDescription(),
                newEventDto.getEventDate(), initiator, newEventDto.getLocation(),
                newEventDto.getPaid(), newEventDto.getParticipantLimit(),
                null, newEventDto.getRequestModeration(), PENDING, newEventDto.getTitle(), 0L);
        log.info("event:" + event);
        return EventMapper.INSTANCE.toEventDto(eventRepository.save(event));
    }

    @Override
    public EventDto getFullEventOwner(Long userId, Long eventId) {
        log.info("Получение информации о событии пользователя {}, событие {}", userId, eventId);
        if (userRepository.existsById(userId)) {
            Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                    "Категория с eventId:" + eventId + "не найдена"));
            if (userId.equals(event.getInitiator().getId())) {
                return EventMapper.INSTANCE.toEventDto(event);
            }
        }
        throw new NotFoundException("пользователь с ид-ом:" + userId + "не найден");
    }

    @Transactional
    @Override
    public EventDto pathEventOwner(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        log.info("Изменение пользователем {} события {} ", userId, eventId);
        userRepository.findById(userId).orElseThrow(RuntimeException::new);
        Event event = eventRepository.findById(eventId).orElseThrow(RuntimeException::new);
        if (event.getState() == PUBLISHED) {
            throw new ConflictException("Нельзя редактировать опубликованные события");
        }
        if (!userId.equals(event.getInitiator().getId())) {
            throw new NotFoundException("событие не принадлежит пользователю");
        }
        if (updateEventUserRequest.getEventDate() != null
                && updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Время редактирования события закончилось");
        }
        if (updateEventUserRequest.getStateAction() == SEND_TO_REVIEW) {
            event.setState(State.PENDING);
        }
        if (updateEventUserRequest.getStateAction() == CANCEL_REVIEW) {
            event.setState(CANCELED);
        }
        if (updateEventUserRequest.getStateAction() == PUBLISH_EVENT) {
            event.setState(PUBLISHED);
        }
        Optional.ofNullable(updateEventUserRequest.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(updateEventUserRequest.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateEventUserRequest.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateEventUserRequest.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateEventUserRequest.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEventUserRequest.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEventUserRequest.getRequestModeration()).ifPresent(event::setRequestModeration);
        Event savedEvent = eventRepository.save(event);
        return EventMapper.INSTANCE.toEventDto(savedEvent);
    }

}
