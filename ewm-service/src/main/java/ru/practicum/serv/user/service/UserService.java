package ru.practicum.serv.user.service;

import ru.practicum.serv.user.dto.UserDto;

import java.util.List;

public interface UserService {
    public Object save(UserDto userDto);

    Object findUsers(List<Long> ids, int from, int size);

    void deleteUser(Long userId);
}
