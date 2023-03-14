package ru.practicum.serv.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.practicum.serv.category.mapper.CategoryMapper;
import ru.practicum.serv.event.dto.EventDto;
import ru.practicum.serv.event.dto.EventShortDto;
import ru.practicum.serv.event.model.Event;
import ru.practicum.serv.user.mapper.UserMapper;

@Mapper(uses = {UserMapper.class, CategoryMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "confirmedRequests", ignore = true)
    EventDto toEventDto(Event event);

    @Mapping(target = "confirmedRequests", ignore = true)
    EventShortDto toEventShortDto(Event event);

}
