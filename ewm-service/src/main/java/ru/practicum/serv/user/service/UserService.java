package ru.practicum.serv.user.service;

import ru.practicum.serv.user.dto.UserDto;
import ru.practicum.serv.user.dto.UserShortDto;

import java.util.List;

public interface UserService {
    UserDto save(UserShortDto userDto);

    List<UserDto> findUsers(List<Long> ids, int from, int size);

    void deleteUser(Long userId);
}
