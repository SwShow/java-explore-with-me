package ru.practicum.serv.compilation.service;

import ru.practicum.serv.compilation.dto.NewCompilationDto;

public interface CompilationService {
    Object save(NewCompilationDto compilationDto);

    void deleteCompById(Long compId);

    Object pathCompilation(Long compId, NewCompilationDto updateReq);

    Object getAllCompilations(Boolean pinned, Integer from, Integer size);

    Object getCompilationById(Long compId);
}
