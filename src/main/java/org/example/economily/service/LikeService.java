package org.example.economily.service;

import lombok.RequiredArgsConstructor;
import org.example.economily.dto.ApiResponse;
import org.example.economily.entity.Article;
import org.example.economily.entity.Like;
import org.example.economily.entity.User;
import org.example.economily.exceptions.ErrorCodes;
import org.example.economily.exceptions.ErrorMessageException;
import org.example.economily.repository.ArticleRepository;
import org.example.economily.repository.LikeRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final ArticleRepository articleRepository;

    public ApiResponse toggle(Long articleId, User user) {

        Article article = this.articleRepository.findById(articleId)
                .orElseThrow(() -> new ErrorMessageException("Article not found", ErrorCodes.NotFound));

        Like like = this.likeRepository.findByArticleAndUser(article, user)
                .orElse(null);

        if (like != null) {

            this.likeRepository.delete(like);
            this.likeRepository.flush();

            //hibernate da edge caselar ko'p bo'lishi mumkin
            // va siz yozgan queryingiz avtromatik bajarilmasligi mumkin.
            // Shu uchun flush ishlatiladi.

            return ApiResponse.builder().
                    message("Successfully deleted")
                    .data(false)
                    .build();
        }

        this.likeRepository.save(new Like(article, user));

        return ApiResponse.builder().
                message("Successfully added")
                .data(true)
                .build();
    }
}
