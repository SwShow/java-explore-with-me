package ru.practicum.serv.apiAuthentic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.serv.event.dto.NewEventDto;
import ru.practicum.serv.event.dto.UpdateEventUserRequest;
import ru.practicum.serv.event.service.EventService;
import ru.practicum.serv.message.service.MessageService;
import ru.practicum.serv.request.dto.EventRequestStatusUpdate;
import ru.practicum.serv.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/users/{userId}/")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AuthenticController {
    private final EventService eventService;
    private final RequestService requestService;
    private final MessageService messageService;

    @GetMapping("events")
    public ResponseEntity<Object> getEventsOwner(@PathVariable @Min(1) Long userId,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                 @Min(1) @RequestParam(defaultValue = "10") int size) {
        log.info("Получение событий текущего пользователя {}", userId);
        return new ResponseEntity<>(eventService.getEventOwner(userId, from, size), HttpStatus.OK);
    }

    @PostMapping("events")
    public ResponseEntity<Object> addEventOwner(@PathVariable @Min(1) Long userId,
                                                @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Добавление нового события пользователем {}", userId);
        return new ResponseEntity<>(eventService.addEventOwner(userId, newEventDto), HttpStatus.CREATED);
    }

    @GetMapping("events/{eventId}")
    public ResponseEntity<Object> getFullEventOwner(@PathVariable @Min(1) Long userId, @PathVariable @Min(1) Long eventId) {
        log.info("Получение информации о событии пользователя {}, событие {}", userId, eventId);
        return new ResponseEntity<>(eventService.getFullEventOwner(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("events/{eventId}")
    public ResponseEntity<Object> pathEventOwner(@PathVariable @Min(1) Long userId, @PathVariable @Min(1) Long eventId,
                                                 @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("Изменение пользователем {} события {} ", userId, eventId);
        return new ResponseEntity<>(eventService.pathEventOwner(userId, eventId, updateEventUserRequest), HttpStatus.OK);
    }

    @GetMapping("events/{eventId}/requests")
    public ResponseEntity<Object> getRequestsInfoUser(@PathVariable @Min(1) Long userId, @PathVariable @Min(1) Long eventId) {
        log.info("Получение информации о своих запросах пользователем {} на участие в событии {}", userId, eventId);
        return new ResponseEntity<>(requestService.getRequestsInfoUser(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("events/{eventId}/requests")
    public ResponseEntity<Object> changeStatusRequest(@PathVariable @Min(1) Long userId, @PathVariable @Min(1) Long eventId,
                                                      @RequestBody EventRequestStatusUpdate statusUpdate) {
        log.info("Изменение статуса заявки на участие пользователя {} в событии {}", userId, eventId);
        return new ResponseEntity<>(requestService.changeStatusRequest(userId, eventId, statusUpdate), HttpStatus.OK);
    }

    @GetMapping("requests")
    public ResponseEntity<Object> getInfoUserRequestOther(@PathVariable @Min(1) Long userId) {
        log.info("Получение информации о заявках пользователем {} на участие в событиях", userId);
        return new ResponseEntity<>(requestService.getInfoUserRequestOther(userId), HttpStatus.OK);
    }

    @PostMapping("requests")
    public ResponseEntity<Object> addRequesrFromUser(@PathVariable Long userId, @RequestParam @Min(1) Long eventId) {
        log.info("Добавление запроса на участие в событии от пользователя {}", userId + "идентификатор события:" + eventId);
        return new ResponseEntity<>(requestService.addRequesrFromUser(userId, eventId), HttpStatus.CREATED);
    }

    @PatchMapping("requests/{requestId}/cancel")
    public ResponseEntity<Object> cancelRequestOwner(@PathVariable @Min(1) Long userId, @PathVariable @Min(1) Long requestId) {
        log.info("Отмена на участие в событии пользователем {} запроса {}", userId, requestId);
        return new ResponseEntity<>(requestService.cancelRequestOwner(userId, requestId), HttpStatus.OK);
    }

    @GetMapping("events/{eventId}/messages")
    public ResponseEntity<Object> getMessages(@PathVariable @Min(1) Long userId, @PathVariable @Min(1) Long eventId) {
        log.info("Получение сообщений пользователю {}, от админа о событии {}", userId, eventId);
        return new ResponseEntity<>(messageService.getInfo(userId, eventId),HttpStatus.OK);
    }
}


