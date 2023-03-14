package ru.practicum.serv.api.Public;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.serv.category.service.CategoryService;
import ru.practicum.serv.compilation.service.CompilationService;
import ru.practicum.serv.event.dto.EventDto;
import ru.practicum.serv.event.service.EventService;
import ru.practicum.serv.statuses.SortEv;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicController {
    private final CompilationService compilationService;
    private final CategoryService categoryService;
    private final EventService eventService;

    @GetMapping("compilations")
    public ResponseEntity<Object> getAllCompilations(@RequestParam(required = false) Boolean pinned,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                     @Min(1) @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос на получение подборок событий");
        return new ResponseEntity<>(compilationService.getAllCompilations(pinned, from, size), HttpStatus.OK);
    }

    @GetMapping("compilations/{compId}")
    public ResponseEntity<Object> getCompilationById(@PathVariable @Min(1) Long compId) {
        log.info("Получен запрос на получение подборки с ид-ом {}", compId);
        return new ResponseEntity<>(compilationService.getCompilationById(compId), HttpStatus.OK);
    }

    @GetMapping("categories")
    public ResponseEntity<Object> getAllCategories(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @Min(1) @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос на получение всех категорий");
        return new ResponseEntity<>(categoryService.getAllCategories(from, size), HttpStatus.OK);
    }

    @GetMapping("categories/{catId}")
    public ResponseEntity<Object> getCategoryById(@PathVariable Long catId) {
        log.info("Получен запрос на получение категории с ид-ом {}", catId);
        return new ResponseEntity<>(categoryService.getCategoryById(catId), HttpStatus.OK);
    }

    @GetMapping("events")
    public ResponseEntity<Object> getAllEvents(@RequestParam(required = false) String text,
                                               @RequestParam(required = false) List<Long> categories,
                                               @RequestParam(required = false) Boolean paid,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                               LocalDateTime rangeStart,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                               LocalDateTime rangeEnd,
                                               @RequestParam(required = false) Boolean onlyAvailable,
                                               @RequestParam(required = false) SortEv sort,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Min(1) @RequestParam(defaultValue = "10") Integer size,
                                               HttpServletRequest httpRequest) {
        log.info("Получен запрос на получение событий с возможностью фильтрации");
        log.info("client ip: {}", httpRequest.getRemoteAddr());
        log.info("endpoint path: {}", httpRequest.getRequestURI());
        return new ResponseEntity<>(eventService.getAllEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                from, size, httpRequest), HttpStatus.OK);
    }

    @GetMapping("events/{id}")
    public ResponseEntity<EventDto> getFullInfoEvent(@Min(1) @PathVariable Long id, HttpServletRequest httpRequest) {
        log.info("Получен запрос на получение инфо о событии с ид-ом {}", id);
        log.info("client ip: {}", httpRequest.getRemoteAddr());
        log.info("endpoint path: {}", httpRequest.getRequestURI());
        return new ResponseEntity<>(eventService.getFullInfoEvent(id, httpRequest), HttpStatus.OK);
    }
}
