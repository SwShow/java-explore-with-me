package ru.practicum.serv.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.practicum.serv.category.mapper.CategoryMapper;
import ru.practicum.serv.event.mapper.EventMapper;
import ru.practicum.serv.request.dto.ParticipationRequestDto;
import ru.practicum.serv.request.model.Request;
import ru.practicum.serv.user.mapper.UserMapper;

@Mapper(uses = {
        UserMapper.class, CategoryMapper.class, EventMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RequestMapper {

    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    @Mapping(target = "requester", source = "requesterId")
    @Mapping(target = "event", source = "event.id")
    ParticipationRequestDto toRequestDto(Request request);

    @Mapping(target = "requester", source = "requesterId")
    @Mapping(target = "event", source = "event.id")
    ParticipationRequestDto toParticipationRequestDto(Request request);
}
