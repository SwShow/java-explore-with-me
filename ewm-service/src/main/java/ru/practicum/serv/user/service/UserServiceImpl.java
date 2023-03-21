package ru.practicum.serv.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.serv.exception.ConflictException;
import ru.practicum.serv.user.dto.UserDto;
import ru.practicum.serv.user.dto.UserShortDto;
import ru.practicum.serv.user.mapper.UserMapper;
import ru.practicum.serv.user.model.User;
import ru.practicum.serv.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto save(UserShortDto userDto) {
        if (userRepository.findByName(userDto.getName()).isPresent()) {
            throw new ConflictException("Это имя уже занято");
        }
        return UserMapper.INSTANCE.toUserDto(userRepository.save(UserMapper.INSTANCE.toUser(userDto)));
    }

    @Override
    public List<UserDto> findUsers(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        if (ids.isEmpty()) {
            return userRepository.findAll(pageable)
                    .stream()
                    .map(UserMapper.INSTANCE::toUserDto)
                    .collect(Collectors.toList());
        }
        return userRepository.findAllByIdIn(ids)
                .stream()
                .map(UserMapper.INSTANCE::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ConflictException("пользователь с ид {} не существует"));
        userRepository.delete(user);
    }
}
