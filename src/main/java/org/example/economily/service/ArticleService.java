package org.example.economily.service;

import lombok.RequiredArgsConstructor;
import org.example.economily.dto.ApiResponse;
import org.example.economily.dto.ArticleDto;
import org.example.economily.entity.Article;
import org.example.economily.entity.User;
import org.example.economily.enums.Topic;
import org.example.economily.exceptions.ErrorCodes;
import org.example.economily.exceptions.ErrorMessageException;
import org.example.economily.mapper.ArticleMapper;
import org.example.economily.repository.ArticleRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;

    public ApiResponse create(ArticleDto.CreateArticle dto, User user) {
        Article article = articleMapper.toEntity(dto);
        article.setCreatedBy(user);
        articleRepository.save(article);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Article created successfully")
                .data(articleMapper.toDto(article))
                .build();
    }

    public ApiResponse getById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ErrorMessageException("Article not found", ErrorCodes.NotFound));

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Article found")
                .data(articleMapper.toDto(article))
                .build();
    }

    public ApiResponse getAll(Topic topic, Pageable pageable) {
        Page<Article> articlePage = this.articleRepository.findAll(topic != null?topic.name():null, pageable);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("List of articles")
                .data(articleMapper.dtoList(articlePage.getContent()))
                .elements(articlePage.getTotalElements())
                .pages(articlePage.getTotalPages())
                .build();
    }

    public ApiResponse update(Long id, ArticleDto.CreateArticle dto) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ErrorMessageException("Article not found", ErrorCodes.NotFound));

        articleMapper.updateEntity(article, dto);
        articleRepository.save(article);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Article updated")
                .data(articleMapper.toDto(article))
                .build();
    }

    public ApiResponse delete(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ErrorMessageException("Article not found", ErrorCodes.NotFound));

        article.setVisibility(false);

        articleRepository.save(article);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Article deleted")
                .build();
    }
}
