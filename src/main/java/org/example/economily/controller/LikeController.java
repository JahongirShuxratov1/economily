package org.example.economily.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.economily.dto.ApiResponse;
import org.example.economily.dto.ArticleDto;
import org.example.economily.entity.User;
import org.example.economily.service.LikeService;
import org.example.economily.util.CurrentUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like")
@Tag(name = "Like", description = "Like endpoints")
public class LikeController {
    private final LikeService service;

    @Operation(
            summary = "toggle like to remove or add it ",
            description = "If return type is false -> it has been removed.True is added."
    )
    @PutMapping("/toggle")
    public ApiResponse toggle(
            @Parameter(description = "Add or remove like") @RequestParam Long articleId,
            @CurrentUser User user
    ) {
        return service.toggle(articleId,user);
    }

}
