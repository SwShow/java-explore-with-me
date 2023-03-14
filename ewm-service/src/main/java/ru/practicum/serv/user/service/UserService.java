package ru.practicum.serv.user.service;

import ru.practicum.serv.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto save(UserDto userDto);

    List<UserDto> findUsers(List<Long> ids, int from, int size);

    void deleteUser(Long userId);
}
