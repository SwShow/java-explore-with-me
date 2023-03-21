package ru.practicum.serv.message.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.serv.event.model.Event;
import ru.practicum.serv.event.repository.EventsRepository;
import ru.practicum.serv.exception.ConflictException;
import ru.practicum.serv.exception.NotFoundException;
import ru.practicum.serv.exception.ValidationException;
import ru.practicum.serv.message.dto.MessageDto;
import ru.practicum.serv.message.mapper.MessageMapper;
import ru.practicum.serv.message.model.Message;
import ru.practicum.serv.message.repository.MessageRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.serv.statuses.State.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final EventsRepository eventRepository;

    @Override
    public MessageDto addMessage(Long eventId, String message) {
        log.info("Получен запрос на добавление сообщения от админа к событию:" + eventId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено"));
        if (event.getState().equals(PUBLISHED) || event.getState().equals(CANCELED)) {
            throw new ConflictException("Невозможно изменить это событие");
        }
        if (message.isEmpty()) {
            throw new ValidationException("Сообщение отсутствует");
        }
        event.setState(AMEND);
        eventRepository.save(event);
        Message mes = new Message(null, eventId, message);
        return MessageMapper.INSTANCE.toMessageDto(messageRepository.save(mes));
    }

    @Override
    public List<MessageDto> getInfo(Long userId, Long eventId) {
        log.info("Получение сообщений пользователю {}, от админа о событии {}", userId, eventId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено"));
        if (event.getInitiator().getId().equals(userId)) {
            return findMessages(eventId);
        }
        throw new ConflictException("Эта информация вам не доступна");
    }

    @Override
    public List<MessageDto> getMessage(Long eventId) {
        log.info("Получение сообщений админа о событии {}", eventId);
        eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено"));
        return findMessages(eventId);
    }

    @Override
    public void deleteAllByEventId(Long eventId) {
        messageRepository.deleteAll(messageRepository.findAllByEventId(eventId));
    }

    public List<MessageDto> findMessages(Long eventId) {

        List<MessageDto> messages =  messageRepository.findAllByEventId(eventId).stream()
                .map(MessageMapper.INSTANCE::toMessageDto)
                .collect(Collectors.toList());
        if (messages.size() == 0) {
            MessageDto mD = new MessageDto(null, eventId, "Сообщения модератора не найдены");
            messages.add(mD);
        }
        return messages;
    }
}
