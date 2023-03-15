package ru.practicum.serv.compilation.service;

import ru.practicum.serv.compilation.dto.CompilationDto;
import ru.practicum.serv.compilation.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto save(NewCompilationDto compilationDto);

    void deleteCompById(Long compId);

    CompilationDto pathCompilation(Long compId, NewCompilationDto updateReq);

    List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compId);
}
