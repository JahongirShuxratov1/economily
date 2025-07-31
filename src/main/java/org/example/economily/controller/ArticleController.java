package org.example.economily.controller;

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
public class ArticleController {

    private final ArticleService service;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ApiResponse create(
            @RequestBody ArticleDto.CreateArticle dto,
            @CurrentUser User user
            ) {
        return service.create(dto,user);
    }

    @GetMapping("get-by-id")
    public ApiResponse getById(
            @RequestParam Long id
    ) {
        return service.getById(id);
    }

    @GetMapping("/all")
    public ApiResponse getAll(
                              @RequestParam(value = "page", defaultValue = "0") Integer page,
                              @RequestParam(value = "size", defaultValue = "10") Integer size,
                              @RequestParam(required = false)Topic topic
                              ) {
        return service.getAll(topic,PageRequest.of(page,size));
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ApiResponse update(
            @RequestParam Long id,
            @RequestParam ArticleDto.CreateArticle dto
    ) {
        return service.update(id,dto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ApiResponse delete(
            @RequestParam Long id
    ) {
        return service.delete(id);
    }
}
