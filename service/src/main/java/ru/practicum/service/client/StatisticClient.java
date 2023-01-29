package ru.practicum.service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatisticClient implements HttpStatisticClient {
    private final RestTemplate rest;
    private final String serverUrl;
    private static final String APP_NAME = "service";
    private static final String API_HIT = "/hit";
    private static final String API_STATS = "/stats";
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    public StatisticClient(@Value("${stat-server.url}") String serverUrl,
                           RestTemplateBuilder builder) {
        this.rest = builder.build();
        this.serverUrl = serverUrl;
        rest.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    @Override
    public HttpStatus addStatistic(String uri, String ip) {
        EndpointHit body = new EndpointHit(APP_NAME, uri, ip, null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<EndpointHit> requestEntity = new HttpEntity<>(body, headers);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(serverUrl + API_HIT);
        ResponseEntity<Object> response = rest.exchange(
                uriBuilder.build().encode().toUri(),
                HttpMethod.POST,
                requestEntity,
                Object.class);
        return response.getStatusCode();
    }

    @Override
    public ResponseEntity<List<ViewStats>> getStatistic(LocalDateTime start, LocalDateTime end,
                                                        String[] uris, Boolean unique) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(serverUrl + API_STATS);
        uriBuilder.queryParam("start", dateTimeToStr(start));
        uriBuilder.queryParam("end", dateTimeToStr(end));
        uriBuilder.queryParam("unique", unique);
        for (String s : uris) {
            uriBuilder.queryParam("uris", s);
        }
        URI uri = uriBuilder.build().encode().toUri();
        return rest.exchange(uri, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<ViewStats>>(){});
    }

    public static String dateTimeToStr(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return formatter.format(value);
    }
 }