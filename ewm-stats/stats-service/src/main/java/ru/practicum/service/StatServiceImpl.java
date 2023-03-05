package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.HitRequest;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.StatRepo;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class StatServiceImpl implements StatService {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatRepo statRepository;

    @Override
    public EndpointHit save(HitRequest hitRequest) {
        EndpointHit hit = EndpointHit.builder()
                .app(hitRequest.getApp())
                .ip(hitRequest.getIp())
                .uri(hitRequest.getUri())
                .timestamp(hitRequest.getTimestamp())
                .build();
        log.info("endpointHit:" + hit);
        return HitMapper.toEndpointHit(statRepository.save(HitMapper.toHit(hit)));
    }

    @Override
    public List<ViewStats> getStats(String start, String end, List<String> uris, Boolean uniq) {
        LocalDateTime startDate = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8),
                DATE_TIME_FORMATTER);
        LocalDateTime endDate = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8),
                DATE_TIME_FORMATTER);
        log.info("startDate:" + startDate + "endDate:" + endDate + "uris:" + uris);
        List<ViewStats> list;
        if (uniq) {
            list = statRepository.findByUriAndUniqueIp(startDate, endDate, uris);
        } else {
            list = statRepository.findByUri(startDate, endDate, uris);
        }
        log.info("list:" + list);
        return list;
    }
}
