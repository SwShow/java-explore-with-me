package ru.practicum.serv.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.serv.event.model.Event;
import ru.practicum.serv.event.repository.EventsRepository;
import ru.practicum.serv.exception.ConflictException;
import ru.practicum.serv.exception.NotFoundException;
import ru.practicum.serv.exception.ValidationException;
import ru.practicum.serv.request.dto.EventRequestStatusUpdate;
import ru.practicum.serv.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.serv.request.dto.ParticipationRequestDto;
import ru.practicum.serv.request.mapper.RequestMapper;
import ru.practicum.serv.request.model.Request;
import ru.practicum.serv.request.repository.RequestRepository;
import ru.practicum.serv.statuses.RequestStatus;
import ru.practicum.serv.user.model.User;
import ru.practicum.serv.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.serv.statuses.RequestStatus.*;
import static ru.practicum.serv.statuses.State.PUBLISHED;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventsRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getRequestsInfoUser(Long userId, Long eventId) {
        log.info("Получение информации о своих запросах пользователем {} на участие в событии {}", userId, eventId);
        if (userRepository.existsById(userId)) {
            Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
            if (!event.getInitiator().getId().equals(userId)) {
                throw new ValidationException("Это не ваше событие");
            }
            return event.getRequests().stream()
                    .map(RequestMapper.INSTANCE::toParticipationRequestDto)
                    .collect(Collectors.toList());

        }
        throw new NotFoundException("Пользователь с id" + userId + "не найден");
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult changeStatusRequest(Long userId, Long eventId, EventRequestStatusUpdate statusUpdate) {
        log.info("Изменение статуса заявки на участие пользователя {} в событии {}", userId, eventId);
        if (statusUpdate.getStatus() == null || statusUpdate.getRequestIds() == null) {
            throw new ConflictException("Нет статуса или идентификаторов для замены");
        }
        userRepository.findById(userId).orElseThrow(RuntimeException::new);
        Event event = eventRepository.findById(eventId).orElseThrow(RuntimeException::new);
        List<Long> requestsId = statusUpdate.getRequestIds();
        for (Long requestId : requestsId) {
            Request request = requestRepository.findById(requestId).orElseThrow(RuntimeException::new);
            if (request.getStatus() != RequestStatus.PENDING) {
                log.info("Статус заявки:" + request.getStatus());
                throw new ConflictException("Статус заявки неизменяемый");
            }
            if (statusUpdate.getStatus() == RequestStatus.CONFIRMED) {
                log.info("Статус для замены:" + statusUpdate.getStatus());
                log.info("Лимит участников:" + event.getParticipantLimit());
                log.info("ПОДТВЕРЖДЕННЫЕ ЗАЯВКИ:" + getConfirmedRequests(event.getRequests()).size());
                if (event.getParticipantLimit() <= getConfirmedRequests(event.getRequests()).size()) {
                    throw new ConflictException("Лимит участников закончился");
                } else {
                    request.setStatus(RequestStatus.CONFIRMED);
                    RequestMapper.INSTANCE.toParticipationRequestDto(requestRepository.save(request));
                }
            } else if (statusUpdate.getStatus() == REJECTED) {
                request.setStatus(REJECTED);
                RequestMapper.INSTANCE.toParticipationRequestDto(requestRepository.save(request));
            }
            log.info("Установлен статус:" + request.getStatus());
            RequestMapper.INSTANCE.toParticipationRequestDto(requestRepository.save(request));
        }
        List<ParticipationRequestDto> confirmedRequests = requestRepository.findAllByIdInAndStatus(requestsId,
                CONFIRMED).stream().map(RequestMapper.INSTANCE::toRequestDto).collect(Collectors.toList());

        List<ParticipationRequestDto> rejectedRequests = requestRepository.findAllByIdInAndStatus(requestsId,
                REJECTED).stream().map(RequestMapper.INSTANCE::toRequestDto).collect(Collectors.toList());
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    public List<Request> getConfirmedRequests(List<Request> requests) {
        return requests.stream().filter(r -> r.getStatus() == CONFIRMED).collect(Collectors.toList());
    }

    @Override
    public List<ParticipationRequestDto> getInfoUserRequestOther(Long userId) {
        log.info("Получение информации о заявках пользователем {} на участие в событиях", userId);
        if (userRepository.existsById(userId)) {
            return requestRepository.findAllByRequesterId(userId)
                    .stream()
                    .map(RequestMapper.INSTANCE::toRequestDto)
                    .collect(Collectors.toList());
        }
        throw new NotFoundException("Пользователь с id" + userId + "не найден");
    }

    @Override
    public ParticipationRequestDto addRequesrFromUser(Long userId, Long eventId) {
        log.info("Добавление запроса на участие в событии от пользователя {}", userId);
        userRepository.findById(userId).orElseThrow(RuntimeException::new);
        Event event = eventRepository.findById(eventId).orElseThrow(RuntimeException::new);
        Optional<Request> request = requestRepository.findByRequesterIdAndEventId(userId, eventId);
        if (request.isPresent()) {
            throw new ConflictException("Повторный запрос");
        }
        if (event.getState() != PUBLISHED) {
            throw new ConflictException("Это событие еще не опубликовано");
        }
        if (userId.equals(event.getInitiator().getId())) {
            throw new ConflictException("Это ваше событие");
        }
        log.info("Лимит участников:" + event.getParticipantLimit());
        log.info("ПОДТВЕРЖДЕННЫЕ ЗАЯВКИ:" + getConfirmedRequests(event.getRequests()).size());
        if (event.getParticipantLimit() <= getConfirmedRequests(event.getRequests()).size()) {
            throw new ConflictException("Лимит участников закончился");

        } else if (event.getRequestModeration() != null && !event.getRequestModeration()) {
            return RequestMapper.INSTANCE.toParticipationRequestDto(requestRepository.save(new Request(
                    0L, LocalDateTime.now(), event, userId, CONFIRMED)));
        } else {
            return RequestMapper.INSTANCE.toParticipationRequestDto(requestRepository.save(new Request(
                    0L, LocalDateTime.now(), event, userId, PENDING)));
        }

    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelRequestOwner(Long userId, Long requestId) {
        log.info("Отмена на участие в событии пользователем {} запроса {}", userId, requestId);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "Пользователь с id" + userId + "не найден"));
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Ваш запрос не найден"));
        if (!user.getId().equals(userId)) {
            throw new NotFoundException("Вы запрашиваете не свой запрос");
        }
        if (request.getStatus() == REJECTED || request.getStatus() == CANCELED) {
            throw new ConflictException("Заявка уже отменена");
        }
        request.setStatus(CANCELED);
        return RequestMapper.INSTANCE.toRequestDto(requestRepository.save(request));
    }
}
