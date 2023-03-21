package ru.practicum.serv.apiAuthentic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.serv.event.dto.EventDto;
import ru.practicum.serv.event.dto.NewEventDto;
import ru.practicum.serv.event.service.EventService;
import ru.practicum.serv.request.dto.EventRequestStatusUpdate;
import ru.practicum.serv.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.serv.request.dto.ParticipationRequestDto;
import ru.practicum.serv.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AuthenticController {
    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping("events")
    public ResponseEntity<List<EventDto>> getEventsOwner(@PathVariable @Min(1) Long userId,
                                                         @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                         @Min(1) @RequestParam(defaultValue = "10") int size) {
        log.info("Получение событий текущего пользователя {}", userId);
        return new ResponseEntity<>(eventService.getEventOwner(userId, from, size), HttpStatus.OK);
    }

    @PostMapping("events")
    public ResponseEntity<EventDto> addEventOwner(@PathVariable @Min(1) Long userId,
                                                @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Добавление нового события пользователем {}", userId);
        return new ResponseEntity<>(eventService.addEventOwner(userId, newEventDto), HttpStatus.CREATED);
    }

    @GetMapping("events/{eventId}")
    public ResponseEntity<EventDto> getFullEventOwner(@PathVariable @Min(1) Long userId, @PathVariable @Min(1) Long eventId) {
        log.info("Получение информации о событии пользователя {}, событие {}", userId, eventId);
        return new ResponseEntity<>(eventService.getFullEventOwner(userId, eventId), HttpStatus.OK);
    }

    @GetMapping("events/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequestsInfoUser(@PathVariable @Min(1) Long userId, @PathVariable @Min(1) Long eventId) {
        log.info("Получение информации о своих запросах пользователем {} на участие в событии {}", userId, eventId);
        return new ResponseEntity<>(requestService.getRequestsInfoUser(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("events/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> changeStatusRequest(@PathVariable @Min(1) Long userId, @PathVariable @Min(1) Long eventId,
                                                                              @RequestBody EventRequestStatusUpdate statusUpdate) {
        log.info("Изменение статуса заявки на участие пользователя {} в событии {}", userId, eventId);
        return new ResponseEntity<>(requestService.changeStatusRequest(userId, eventId, statusUpdate), HttpStatus.OK);
    }

    @GetMapping("requests")
    public ResponseEntity<List<ParticipationRequestDto>> getInfoUserRequestOther(@PathVariable @Min(1) Long userId) {
        log.info("Получение информации о заявках пользователем {} на участие в событиях", userId);
        return new ResponseEntity<>(requestService.getInfoUserRequestOther(userId), HttpStatus.OK);
    }

    @PostMapping("requests")
    public ResponseEntity<ParticipationRequestDto> addRequestFromUser(@PathVariable Long userId, @RequestParam @Min(1) Long eventId) {
        log.info("Добавление запроса на участие в событии от пользователя {}", userId + "идентификатор события:" + eventId);
        return new ResponseEntity<>(requestService.addRequestFromUser(userId, eventId), HttpStatus.CREATED);
    }

    @PatchMapping("requests/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequestOwner(@PathVariable @Min(1) Long userId, @PathVariable @Min(1) Long requestId) {
        log.info("Отмена на участие в событии пользователем {} запроса {}", userId, requestId);
        return new ResponseEntity<>(requestService.cancelRequestOwner(userId, requestId), HttpStatus.OK);
    }

}


