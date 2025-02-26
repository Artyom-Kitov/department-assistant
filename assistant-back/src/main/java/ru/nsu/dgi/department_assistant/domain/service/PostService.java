package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.dto.employee.PostRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.PostResponseDto;

import java.util.List;

public interface PostService {
    List<PostResponseDto> getAll();

    PostResponseDto getById(Integer id);

    PostResponseDto create(PostRequestDto postRequestDto);

    PostResponseDto update(Integer id, PostRequestDto postRequestDto);

    void delete(Integer id);
}
