package ru.practicum.serv.message.service;


import ru.practicum.serv.message.dto.MessageDto;

import java.util.List;

public interface MessageService {
    MessageDto addMessage(Long eventId, String message);

    List<MessageDto> getInfo(Long userId, Long eventId);

    List<MessageDto> getMessage(Long eventId);

    void deleteAllByEventId(Long eventId);
    }
