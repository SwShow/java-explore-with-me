package ru.practicum.serv.message.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.serv.message.dto.MessageDto;
import ru.practicum.serv.message.model.Message;

@Mapper
public interface MessageMapper {

    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    Message toMessage(MessageDto MessageDto);

    MessageDto toMessageDto(Message message);
}
