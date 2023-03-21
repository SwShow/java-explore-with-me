package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.HitRequest;
import ru.practicum.model.ViewStats;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class StatistClient {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final WebClient webClient;

    public StatistClient(String url) {
        this.webClient = WebClient.create(url);
    }

    public void addHit(HttpServletRequest httpRequest) {
        HitRequest hitRequest = HitRequest.builder()
                .app("ewm-service")
                .ip(httpRequest.getRemoteAddr())
                .uri(httpRequest.getRequestURI())
                .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                .build();
        log.info("IP:" + hitRequest.getIp());
        log.info("URI:" + hitRequest.getUri());

        webClient.post()
                .uri("/hit")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(hitRequest)
                .retrieve()
                .bodyToMono(EndpointHit.class)
                .block();
    }

    public List<ViewStats> getListStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/stats")
                        .queryParam("start", start.format(DATE_TIME_FORMATTER))
                        .queryParam("end", end.format(DATE_TIME_FORMATTER))
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .bodyToFlux(ViewStats.class)
                .collectList()
                .block();
    }

}
