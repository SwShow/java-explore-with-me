package ru.practicum.serv.request.dto;

import lombok.*;
import ru.practicum.serv.statuses.RequestStatus;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdate {
    private List<Long> requestIds;
    private RequestStatus status;
}

