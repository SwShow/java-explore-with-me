package ru.practicum.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    private String ip;

    private String uri;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String timestamp;

}