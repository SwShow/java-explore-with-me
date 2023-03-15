package ru.practicum.serv.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    @Positive(message = "Отрицательный идентификатор")
    private Long id;

    @NotEmpty(message = "Имя не может быть пустым")
    private String name;
}
