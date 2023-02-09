package ru.practicum.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class HitMapper {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

     public static Hit toHit(EndpointHit endpointHit) {
        return new Hit(endpointHit.getId(), endpointHit.getApp(), endpointHit.getIp(),
                endpointHit.getUri(), LocalDateTime.parse(endpointHit.getTimestamp(), DATE_TIME_FORMATTER));
    }

    public static EndpointHit toEndpointHit(Hit hit) {
        return new EndpointHit(hit.getId(), hit.getApp(), hit.getUri(), hit.getIp(),
                hit.getTimestamp().format(DATE_TIME_FORMATTER));
    }
}