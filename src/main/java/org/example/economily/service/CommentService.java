package org.example.economily.service;

import lombok.RequiredArgsConstructor;
import org.example.economily.dto.ApiResponse;
import org.example.economily.dto.CommentDto;
import org.example.economily.entity.Article;
import org.example.economily.entity.Comment;
import org.example.economily.entity.User;
import org.example.economily.exceptions.ErrorCodes;
import org.example.economily.exceptions.ErrorMessageException;
import org.example.economily.mapper.CommentMapper;
import org.example.economily.repository.ArticleRepository;
import org.example.economily.repository.CommentRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ArticleRepository articleRepository;

    public ApiResponse create(CommentDto.CreateComment dto, User user) {
        Article article = articleRepository.findById(dto.getArticleId())
                .orElseThrow(() -> new ErrorMessageException("Article not found", ErrorCodes.NotFound));

        Comment comment = commentMapper.toEntity(dto, user,article);
        commentRepository.save(comment);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Comment created")
                .data(commentMapper.toDto(comment))
                .build();
    }

    public ApiResponse getByArticle(Long articleId, Pageable pageable) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ErrorMessageException("Article not found", ErrorCodes.NotFound));

        Page<Comment> commentPage = commentRepository.findAllByArticle(article, pageable);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("List of comments for the article")
                .data(commentMapper.dtoList(commentPage.getContent()))
                .elements(commentPage.getTotalElements())
                .pages(commentPage.getTotalPages())
                .build();
    }

    public ApiResponse update(Long commentId, CommentDto.UpdateComment dto, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ErrorMessageException("Comment not found", ErrorCodes.NotFound));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new ErrorMessageException("You do not own this comment", ErrorCodes.Forbidden);
        }

        commentMapper.update(comment, dto);
        commentRepository.save(comment);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Comment updated")
                .data(commentMapper.toDto(comment))
                .build();
    }

    public ApiResponse delete(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ErrorMessageException("Comment not found", ErrorCodes.NotFound));

        if (!user.isAdmin() && !user.isSuperAdmin()&&!(Objects.equals(user.getId(), comment.getUser().getId()))) {
            throw new ErrorMessageException("Access denied", ErrorCodes.Forbidden);
        }

        commentRepository.delete(comment);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Comment deleted")
                .build();
    }
}
