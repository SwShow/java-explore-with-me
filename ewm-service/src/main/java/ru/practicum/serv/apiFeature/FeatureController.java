package ru.practicum.serv.apiFeature;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.serv.event.dto.EventDto;
import ru.practicum.serv.event.dto.UpdateEventAdminRequest;
import ru.practicum.serv.event.dto.UpdateEventUserRequest;
import ru.practicum.serv.event.service.EventService;
import ru.practicum.serv.message.dto.MessageDto;
import ru.practicum.serv.message.service.MessageService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
@Validated
public class FeatureController {

    private final MessageService messageService;
    private final EventService eventService;

    @GetMapping("users/{userId}/events/{eventId}/messages")
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable @Min(1) Long userId, @PathVariable @Min(1) Long eventId) {
        log.info("Получение сообщений пользователю {}, от админа о событии {}", userId, eventId);
        return new ResponseEntity<>(messageService.getInfo(userId, eventId), HttpStatus.OK);
    }

    @PostMapping("admin/message/{eventId}")
    public ResponseEntity<MessageDto> addMessage(@PathVariable @Min(1) Long eventId,
                                             @RequestBody String message) {
        log.info("Получен запрос на добавление сообщения от админа к событию:" + eventId);
        return new ResponseEntity<>(messageService.addMessage(eventId, message), HttpStatus.CREATED);
    }

    @GetMapping("admin/events/{eventId}/messages")
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable @Min(1) Long eventId) {
        log.info("Получение сообщений админа о событии {}", eventId);
        return new ResponseEntity<>(messageService.getMessage(eventId),HttpStatus.OK);
    }

    @PatchMapping("admin/events/{eventId}")
    public ResponseEntity<EventDto> patchEvent(@PathVariable @Min(1) Long eventId,
                                             @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("получен запрос на редактирование данных события с id {} админом", eventId);
        // В метод добавлена новая функциональность
        return new ResponseEntity<>(eventService.pathEventByAdmin(eventId, updateEventAdminRequest), HttpStatus.OK);
    }

    @PatchMapping("users/{userId}/events/{eventId}")
    public ResponseEntity<EventDto> pathEventOwner(@PathVariable @Min(1) Long userId, @PathVariable @Min(1) Long eventId,
                                                   @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("Изменение пользователем {} события {} ", userId, eventId);
        // В метод добавлена новая функциональность
        return new ResponseEntity<>(eventService.pathEventOwner(userId, eventId, updateEventUserRequest), HttpStatus.OK);
    }
}
