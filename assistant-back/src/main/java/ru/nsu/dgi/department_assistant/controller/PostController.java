package ru.nsu.dgi.department_assistant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Posts",
        description = "Provides basic operations for creating, updating, deleting and retrieving " +
                "information about posts."
)
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    @Operation(
            summary = "Returns all posts",
            description = "Returns all existing posts."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved"
                    )
            }
    )
    @GetMapping()
    public ResponseEntity<List<PostResponseDto>> getAll() {
        return ResponseEntity.ok(postService.getAll());
    }

    @Operation(
            summary = "Returns a post",
            description = "Returns a post by its id."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Entity not found"
                    )
            }
    )
    @GetMapping("/id")
    public ResponseEntity<PostResponseDto> getById(@RequestParam("id") Integer id) {
        return ResponseEntity.ok(postService.getById(id));
    }

    @Operation(
            summary = "Creates a post",
            description =
                    "Creates a new post by its name."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successfully created"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Entity not found"
                    )
            }
    )
    @PostMapping("/create")
    public ResponseEntity<PostResponseDto> create(
            @RequestBody PostRequestDto postRequestDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(postService.create(postRequestDto));
    }

    @Operation(
            summary = "Updates a post",
            description =
                    "Updates a post by its id."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Entity not found"
                    )
            }
    )
    @PutMapping("/update")
    public ResponseEntity<PostResponseDto> update(
            @RequestParam("id") Integer id,
            @RequestBody PostRequestDto postRequestDto
    ) {
        return ResponseEntity.ok(postService.update(id, postRequestDto));
    }

    @Operation(
            summary = "Deletes a post",
            description = "Deletes a post by its id." +
                    "Returns no content response."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successfully deleted"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Entity not found"
                    )
            }
    )
    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam("id") Integer id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
