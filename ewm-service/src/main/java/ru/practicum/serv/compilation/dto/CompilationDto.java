package ru.practicum.serv.compilation.dto;


import lombok.*;
import ru.practicum.serv.event.dto.EventDto;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    private Long id;
    private Boolean pinned;
    @NotBlank(message = "Описание не может быть пустым")
    private String title;
    private List<EventDto> events;
}
