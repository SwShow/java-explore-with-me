package ru.practicum.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Validated
@Getter
@Setter
@Builder
public class EndpointHit {
    private Integer id;

    private String app;

    private String uri;

    private String ip;

    private String timestamp;

}