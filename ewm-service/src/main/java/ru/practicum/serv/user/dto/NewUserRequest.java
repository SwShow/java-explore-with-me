package ru.practicum.serv.user.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {

    @NotBlank(message = "Имя не может быть пустым")
    private String name;
    @NotBlank(message = "Адрес не может быть пустым")
    private String email;
}
