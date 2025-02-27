package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.dgi.department_assistant.domain.dto.employee.PostRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.PostResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Post;
import ru.nsu.dgi.department_assistant.domain.exception.EntityNotFoundException;
import ru.nsu.dgi.department_assistant.domain.mapper.employee.PostMapper;
import ru.nsu.dgi.department_assistant.domain.repository.employee.PostRepository;
import ru.nsu.dgi.department_assistant.domain.service.PostService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PostResponseDto> getAll() {
        log.info("finding all posts");
        List<Post> posts = postRepository.findAll();
        log.info("successfully found {} posts", posts.size());

        return posts.stream()
                .map(postMapper::entityToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponseDto getById(Integer id) {
        log.info("finding post by id {}", id);
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        log.info("successfully found post {}", post);

        return postMapper.entityToResponseDto(post);
    }

    @Override
    @Transactional
    public PostResponseDto create(PostRequestDto postRequestDto) {
        log.info("creating post {}", postRequestDto.name());
        Post post = postMapper.toEntity(postRequestDto);
        post = postRepository.save(post);
        log.info("successfully created post {}", postRequestDto.name());

        return postMapper.entityToResponseDto(post);
    }

    @Override
    @Transactional
    public PostResponseDto update(Integer id, PostRequestDto postRequestDto) {
        log.info("updating post {}", id);
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        postMapper.updateRequestToEntity(postRequestDto, post);
        post = postRepository.save(post);
        log.info("successfully updated post {}", id);

        return postMapper.entityToResponseDto(post);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.info("deleting post {}", id);
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        postRepository.delete(post);
        log.info("successfully deleted post {}", id);
    }
}
