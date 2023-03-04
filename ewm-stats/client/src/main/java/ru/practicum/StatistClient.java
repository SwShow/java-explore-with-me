package ru.practicum;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.HitRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatistClient {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final WebClient webClient;

    public StatistClient(String url) {
        this.webClient = WebClient.create(url);
    }

    public void addHit(HttpServletRequest httpRequest) {
        HitRequest hitRequest = new HitRequest(
                "ewm-service",
                httpRequest.getRequestURI(),
                httpRequest.getRemoteAddr(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER)
        );
        webClient.post()
                .uri("/hit")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(hitRequest)
                .retrieve()
                .bodyToMono(EndpointHit.class)
                .block();
    }

}
