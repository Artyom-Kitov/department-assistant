package ru.nsu.dgi.department_assistant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.dgi.department_assistant.domain.dto.employee.PostRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.PostResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.PostService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    @GetMapping()
    public ResponseEntity<List<PostResponseDto>> getAll() {
        return ResponseEntity.ok(postService.getAll());
    }

    @GetMapping("/id")
    public ResponseEntity<PostResponseDto> getById(@RequestParam("id") Integer id) {
        return ResponseEntity.ok(postService.getById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<PostResponseDto> create(
            @RequestBody PostRequestDto postRequestDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(postService.create(postRequestDto));
    }

    @PutMapping("/update")
    public ResponseEntity<PostResponseDto> update(
            @RequestParam("id") Integer id,
            @RequestBody PostRequestDto postRequestDto
    ) {
        return ResponseEntity.ok(postService.update(id, postRequestDto));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam("id") Integer id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
