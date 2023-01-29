package ru.yandex.practicum.stat.service;

import ru.yandex.practicum.stat.model.EndpointHit;
import ru.yandex.practicum.stat.model.ViewStats;

import java.util.List;

public interface StatService {

    void saveHit(EndpointHit endpointHit);

    List<ViewStats> getStats(String start, String end, String[] uris, Boolean uniq);
}
