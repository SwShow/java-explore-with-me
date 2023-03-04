package ru.practicum.model;

import lombok.*;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHit {
    private long id;

    private String app;

    private String uri;

    private String ip;

    private String timestamp;

}