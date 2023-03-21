package ru.practicum.serv.apiAdmin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.serv.category.dto.CategoryDto;
import ru.practicum.serv.category.dto.NewCategoryDto;
import ru.practicum.serv.category.service.CategoryService;
import ru.practicum.serv.compilation.dto.CompilationDto;
import ru.practicum.serv.compilation.dto.NewCompilationDto;
import ru.practicum.serv.compilation.service.CompilationService;
import ru.practicum.serv.event.dto.EventDto;
import ru.practicum.serv.event.service.EventService;
import ru.practicum.serv.user.dto.UserDto;
import ru.practicum.serv.user.dto.UserShortDto;
import ru.practicum.serv.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminController {
    private final UserService userService;
    private final CategoryService categoryService;
    private final EventService eventService;
    private final CompilationService compilationService;

    @PostMapping("/users")
    public ResponseEntity<UserDto> save(@RequestBody @Valid UserShortDto userDto) {
        log.info("получен запрос на добавление пользователя:" + userDto);
        return new ResponseEntity<>(userService.save(userDto), HttpStatus.CREATED);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> findUsersOfIds(@RequestParam List<Long> ids, @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                 @Min(1) @RequestParam(defaultValue = "10") int size) {
        log.info("получен запрос на получение пользователей:" + ids);
        return new ResponseEntity<>(userService.findUsers(ids, from, size), HttpStatus.OK);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Min(1) Long userId) {
        log.info("получен запрос на удаление пользователя:" + userId);
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> addCategory(@RequestBody @Valid NewCategoryDto category) {
        log.info("получен запрос на добавление категории:" + category);
        return new ResponseEntity<>(categoryService.addCategory(category), HttpStatus.CREATED);
    }

    @DeleteMapping("/categories/{catId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable @Min(1) Long catId) {
        log.info("получен запрос на удаление категории: {}", catId);
        categoryService.deleteCat(catId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/categories/{catId}")  // back CategoryDto
    public ResponseEntity<CategoryDto> pathCategory(@PathVariable @Min(1) Long catId,
                                               @RequestBody @Valid NewCategoryDto dto) {
        log.info("получен запрос на изменение категории: {}", catId + "cat:" + dto);
        return new ResponseEntity<>(categoryService.pathCat(catId, dto), HttpStatus.OK);
    }

    @GetMapping("/events")
    public ResponseEntity<Collection<EventDto>> getEvents(@RequestParam(required = false) List<Long> users,
                                                          @RequestParam(required = false) List<String> states,
                                                          @RequestParam(required = false) List<Long> categories,
                                                          @RequestParam(required = false)
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                          @RequestParam(required = false)
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                          @RequestParam(defaultValue = "0") Integer from,
                                                          @RequestParam(defaultValue = "10") Integer size) {
        log.info("получен запрос от администратора на получение событий в категориях {}", categories);
        return new ResponseEntity<>(eventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd,
                from, size), HttpStatus.OK);
    }

    @PostMapping("/compilations")
    public ResponseEntity<CompilationDto> addCompilation(@RequestBody @Valid NewCompilationDto compilationDto) {
        log.info("получен запрос на добавление новой подборки админом");
        return new ResponseEntity<>(compilationService.save(compilationDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/compilations/{compId}")
    public ResponseEntity<Void> deleteCompilation(@PathVariable @Min(1) Long compId) {
        log.info("получен запрос на удаление подборки с ид-ом {}", compId);
        compilationService.deleteCompById(compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/compilations/{compId}")
    public ResponseEntity<CompilationDto> patchCompilation(@PathVariable @Min(1) Long compId,
                                                   @RequestBody NewCompilationDto updateReq) {
        log.info("получен запрос на изменение подборки с ид-ом {}", compId);
        return new ResponseEntity<>(compilationService.pathCompilation(compId, updateReq), HttpStatus.OK);
    }

}
