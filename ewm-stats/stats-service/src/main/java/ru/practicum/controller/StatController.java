package ru.practicum.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;
import ru.practicum.service.StatService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController("/")
@Validated
@RequiredArgsConstructor
public class StatController {

    private final StatService statService;

    @PostMapping("hit")
    public ResponseEntity<Object> saveHit(@RequestBody @Valid EndpointHit endpoint) {
        log.info("save endpointHit uri {}", endpoint.getUri());
        return new ResponseEntity<>(statService.save(endpoint), HttpStatus.CREATED);
    }

    @GetMapping("stats") // List<ViewStats>
    public  List<ViewStats> getStats(@RequestParam String start,
                                     @RequestParam String end,
                                     @RequestParam List<String> uris,
                                     @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("get statistic for uris:" + uris + "start:" + start + "end:" + end);

        return statService.getStats(start, end, uris, unique);
    }
}
