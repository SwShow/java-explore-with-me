package ru.practicum.serv.request.service;

import ru.practicum.serv.request.dto.EventRequestStatusUpdate;
import ru.practicum.serv.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.serv.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    EventRequestStatusUpdateResult changeStatusRequest(Long userId, Long eventId, EventRequestStatusUpdate statusUpdate);

    List<ParticipationRequestDto> getInfoUserRequestOther(Long userId);

    ParticipationRequestDto addRequesrFromUser(Long userId, Long eventId);

    ParticipationRequestDto cancelRequestOwner(Long userId, Long requestId);

    List<ParticipationRequestDto> getRequestsInfoUser(Long userId, Long eventId);
}
