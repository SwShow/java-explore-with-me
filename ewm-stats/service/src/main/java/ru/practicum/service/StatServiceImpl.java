package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.StatRepository;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class StatServiceImpl implements StatService {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatRepository statRepository;

    @Transactional
    @Override
    public void save(EndpointHit hit) {
        statRepository.save(HitMapper.toHit(hit));
    }

    @Override
    public List<ViewStats> getStats(String start, String end, String[] uris, Boolean uniq) {
        LocalDateTime startDate = LocalDateTime.parse(start, DATE_TIME_FORMATTER);
        LocalDateTime endDate = LocalDateTime.parse(end, DATE_TIME_FORMATTER);
        List<ViewStats> viewStats;

        if (uniq) {
            viewStats = statRepository.getDistinctStats(startDate, endDate, uris);
        } else {
            viewStats = statRepository.getStats(startDate, endDate, uris);
        }
        log.debug("Get stats counts {}", viewStats.size());
        return viewStats;
    }

}
