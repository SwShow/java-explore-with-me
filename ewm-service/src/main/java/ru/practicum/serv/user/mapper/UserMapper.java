package ru.practicum.serv.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.serv.user.dto.UserDto;
import ru.practicum.serv.user.dto.UserShortDto;
import ru.practicum.serv.user.model.User;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    User toUser(UserShortDto userDto);

}
