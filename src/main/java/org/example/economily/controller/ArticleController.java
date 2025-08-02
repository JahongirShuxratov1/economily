package org.example.economily.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.economily.dto.ApiResponse;
import org.example.economily.dto.ArticleDto;
import org.example.economily.entity.User;
import org.example.economily.enums.Topic;
import org.example.economily.service.ArticleService;
import org.example.economily.util.CurrentUser;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/article")
@Tag(name = "Article", description = "Endpoints for managing articles")
public class ArticleController {

    private final ArticleService service;

    @Operation(
            summary = "Create an article",
            description = "Allows an admin to create a new article with a title, content, and topic."
    )
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ApiResponse create(
            @RequestBody ArticleDto.CreateArticle dto,
            @CurrentUser User user
    ) {
        return service.create(dto, user);
    }

    @Operation(
            summary = "Get article by ID",
            description = "Fetches the article details using its unique ID."
    )
    @GetMapping("/get-by-id")
    public ApiResponse getById(
            @Parameter(description = "ID of the article to retrieve") @RequestParam Long id
    ) {
        return service.getById(id);
    }

    @Operation(
            summary = "Get all articles",
            description = "Retrieves all articles optionally filtered by topic, with pagination."
    )
    @GetMapping("/all")
    public ApiResponse getAll(
            @Parameter(description = "Page number (0-based)") @RequestParam(value = "page", defaultValue = "0") Integer page,
            @Parameter(description = "Page size") @RequestParam(value = "size", defaultValue = "10") Integer size,
            @Parameter(description = "Filter by topic") @RequestParam(required = false) Topic topic
    ) {
        return service.getAll(topic, PageRequest.of(page, size));
    }

    @Operation(
            summary = "Update an article",
            description = "Allows an admin to update an existing article by its ID."
    )
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ApiResponse update(
            @Parameter(description = "ID of the article to update") @RequestParam Long id,
            @RequestBody ArticleDto.CreateArticle dto
    ) {
        return service.update(id, dto);
    }

    @Operation(
            summary = "Delete an article",
            description = "Allows an admin to delete an article by its ID."
    )
    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ApiResponse delete(
            @RequestParam Long id
    ) {
        return service.delete(id);
    }
}
