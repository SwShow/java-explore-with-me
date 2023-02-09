package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.StatsRepository;

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
    private final StatsRepository statRepository;

    @Override
    public EndpointHit save(EndpointHit hit) {
        EndpointHit endpointHit = HitMapper.toEndpointHit(statRepository.save(HitMapper.toHit(hit)));
        log.info("endpointHit:" + endpointHit);
        return endpointHit;
    }

    @Override
    public List<ViewStats> getStats(String start, String end, List<String> uris, Boolean uniq) {
        LocalDateTime startDate = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8),
                DATE_TIME_FORMATTER);
        LocalDateTime endDate = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8),
                DATE_TIME_FORMATTER);
        log.info("startDate:" + startDate + "endDate:" + endDate + "uris:" + uris);
        if (uniq) {
            return statRepository.findByUriAndUniqueIp(startDate, endDate, uris);
        } else {
            return statRepository.findByUri(startDate, endDate, uris);
        }
    }
}
