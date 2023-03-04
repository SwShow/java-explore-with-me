package ru.practicum.serv.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.serv.compilation.dto.CompilationDto;
import ru.practicum.serv.compilation.dto.NewCompilationDto;
import ru.practicum.serv.compilation.mapper.CompilationMapper;
import ru.practicum.serv.compilation.model.Compilation;
import ru.practicum.serv.compilation.repository.CompilationRepository;
import ru.practicum.serv.event.dto.EventDto;
import ru.practicum.serv.event.mapper.EventMapper;
import ru.practicum.serv.event.model.Event;
import ru.practicum.serv.event.repository.EventsRepository;
import ru.practicum.serv.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final EventsRepository eventRepository;
    private final CompilationRepository compilationRepository;

    @Override
    public Object save(NewCompilationDto compilationDto) {
        log.info("получен запрос на добавление новой подборки админом");
        List<Event> events = eventRepository.findAllByIdIn(compilationDto.getEvents());
        if (compilationDto.getEvents().size() != events.size()) {
            throw new NotFoundException("События не найдены");
        }
        Compilation compilation = new Compilation(0L, compilationDto.getPinned(), compilationDto.getTitle(), events);
        return CompilationMapper.INSTANCE.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public void deleteCompById(Long compId) {
        log.info("получен запрос на удаление подборки с ид-ом {}", compId);
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Подборка с id" + compId + "не найдена"));
        compilationRepository.delete(compilation);
    }

    @Override
    public CompilationDto pathCompilation(Long compId, NewCompilationDto updateComp) {
        log.info("получен запрос на изменение подборки с ид-ом {}", compId);
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Подборка с id" + compId + "не найдена"));
        if (updateComp.getTitle() != null) {
            compilation.setTitle(updateComp.getTitle());
        }
        if (updateComp.getPinned() != null) {
            compilation.setPinned(updateComp.getPinned());
        }
        if (updateComp.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(updateComp.getEvents()));
        }
        compilationRepository.save(compilation);
        return CompilationMapper.INSTANCE.toCompilationDto(compilation);
    }

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        log.info("Получен запрос на получение подборок событий");
        Pageable pageable = PageRequest.of(from, size);

        List<CompilationDto> compilations = compilationRepository.findAll(pageable).stream()
                .filter(comp -> comp.getPinned() == pinned)
                .map(CompilationMapper.INSTANCE::toCompilationDto)
                .collect(Collectors.toList());

        /*List<Event> events = compilations.stream()
                .map(Compilation::getEvents)
                .flatMap(Collection::stream)
                .collect(Collectors.toList())*/
        return compilations;
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        log.info("Получен запрос на получение подборки с ид-ом {}", compId);
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Подборка с id" + compId + "не найдена"));
        log.info("List Events:" + compilation.getEvents());
        List<EventDto> events = compilation.getEvents()
                .stream()
                .map(EventMapper.INSTANCE::toEventDto)
                .collect(Collectors.toList());
        CompilationDto dto = CompilationMapper.INSTANCE.toCompilationDto(compilation);
        dto.setEvents(events);
        return dto;
    }
}
