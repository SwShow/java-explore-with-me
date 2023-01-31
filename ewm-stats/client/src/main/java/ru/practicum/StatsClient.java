/*package ru.practicum;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.model.EndpointHit;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class StatsClient {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String application;
    private final String statsServiceUri;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public StatsClient(@Value("${spring.application.name}") String application,
        @Value("${stat.service-stat.uri:http://localhost:9090}") String statsServiceUri,
                       ObjectMapper objectMapper) {
        this.application= application;
        this.statsServiceUri = statsServiceUri;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
    }

    public void hit(HttpServletRequest request) {
        EndpointHit hit = EndpointHit.builder()
                .app(application)
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(String.valueOf(LocalDateTime.now()))
                .build();
    }
}*/
