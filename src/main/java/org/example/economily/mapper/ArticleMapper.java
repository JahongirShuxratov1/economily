package org.example.economily.mapper;

import lombok.RequiredArgsConstructor;
import org.example.economily.dto.ArticleDto;
import org.example.economily.dto.UserDto;
import org.example.economily.entity.Article;
import org.example.economily.entity.UserRole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ArticleMapper {
    private final UserMapper userMapper;

    public ArticleDto toDto(Article article) {
        return ArticleDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .subtitle(article.getSubtitle())
                .text(article.getText())
                .topic(article.getTopic())
                .createdAt(article.getCreatedAt())
                .createdBy(
                        article.getCreatedBy() == null ? null : userMapper.toDto(article.getCreatedBy())
                )
                .build();
    }

    public Article toEntity(ArticleDto.CreateArticle dto) {
        return Article.builder()
                .title(dto.getTitle())
                .subtitle(dto.getSubtitle())
                .text(dto.getText())
                .topic(dto.getTopic())
                .build();
    }

    public List<ArticleDto> dtoList(List<Article> articles) {
        return articles.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public void updateEntity(Article article, ArticleDto.CreateArticle dto) {
        if (dto.getTitle() != null) {
            article.setTitle(dto.getTitle());
        }
        if (dto.getSubtitle() != null) {
            article.setSubtitle(dto.getSubtitle());
        }
        if (dto.getText() != null) {
            article.setText(dto.getText());
        }
        if (dto.getTopic() != null) {
            article.setTopic(dto.getTopic());
        }
    }

}
