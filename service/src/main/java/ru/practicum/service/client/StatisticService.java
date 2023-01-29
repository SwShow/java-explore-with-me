package ru.practicum.service.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final HttpStatisticClient httpStatisticClient;
}
