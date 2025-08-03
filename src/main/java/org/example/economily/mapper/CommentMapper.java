package org.example.economily.mapper;

import org.example.economily.dto.CommentDto;
import org.example.economily.entity.Article;
import org.example.economily.entity.Comment;
import org.example.economily.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {

    public Comment toEntity(CommentDto.CreateComment dto, User user,Article article) {
        return Comment.builder()
                .text(dto.getText())
                .article(article)
                .user(user)
                .build();
    }

    public void update(Comment comment, CommentDto.UpdateComment dto) {
        comment.setText(dto.getText());
    }

    public CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .articleId(comment.getArticle().getId())
                .userId(comment.getUser().getId())
                .userFullName(comment.getUser().getFullname())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public List<CommentDto> dtoList(List<Comment> comments) {
        return comments.stream().map(this::toDto).toList();
    }
}
