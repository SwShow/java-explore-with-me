package ru.practicum.serv.user.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserShortDto {

    @NotEmpty
    private String name;

    @NotEmpty
    private String email;
}
