package org.example.economily.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.economily.dto.ApiResponse;
import org.example.economily.dto.CommentDto;
import org.example.economily.entity.User;
import org.example.economily.service.CommentService;
import org.example.economily.util.CurrentUser;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
@Tag(name = "Comment", description = "Endpoints for managing comments")
public class CommentController {

    private final CommentService commentService;

    @Operation(
            summary = "Create a comment",
            description = "Allows an authenticated user to create a comment on an article."
    )
    @PostMapping("/create")
    public ApiResponse create(
            @RequestBody CommentDto.CreateComment dto,
            @CurrentUser User user
    ) {
        return commentService.create(dto, user);
    }

    @Operation(
            summary = "Get comments by article",
            description = "Retrieves all comments for a given article with pagination support."
    )
    @GetMapping("/by-article")
    public ApiResponse getByArticle(
            @Parameter(description = "Article ID to filter comments by") @RequestParam Long articleId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size
    ) {
        return commentService.getByArticle(articleId, PageRequest.of(page, size));
    }

    @Operation(
            summary = "Update a comment",
            description = "Allows the owner of a comment to update its content."
    )
    @PutMapping("/update")
    public ApiResponse update(
            @Parameter(description = "ID of the comment to update") @RequestParam Long commentId,
            @RequestBody CommentDto.UpdateComment dto,
            @CurrentUser User user
    ) {
        return commentService.update(commentId, dto, user);
    }

    @Operation(
            summary = "Delete a comment",
            description = "Allows the owner or an admin to delete a comment."
    )
    @DeleteMapping("/delete")
    public ApiResponse delete(
            @Parameter(description = "ID of the comment to delete") @RequestParam Long commentId,
            @CurrentUser User user
    ) {
        return commentService.delete(commentId, user);
    }
}
