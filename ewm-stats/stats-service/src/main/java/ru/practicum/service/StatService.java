package ru.practicum.service;

import ru.practicum.model.EndpointHit;
import ru.practicum.model.HitRequest;
import ru.practicum.model.ViewStats;

import java.util.List;

public interface StatService {

    EndpointHit save(HitRequest endpointHit);

    List<ViewStats> getStats(String start, String end, List<String> uris, Boolean uniq);
}
