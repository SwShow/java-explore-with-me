package ru.practicum.service;

import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.util.List;

public interface StatService {

    void save(EndpointHit endpointHit);

    List<ViewStats> getStats(String start, String end, String[] uris, Boolean uniq);
}
