package org.example.economily.dto;

import lombok.*;
import org.example.economily.enums.Topic;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleDto {
    private Long id;
    private String title;
    private String subtitle;
    private String text;
    private Topic topic;
    private UserDto createdBy;
    private LocalDateTime createdAt;

    private List<LikeDto> likes;
    private List<CommentDto> comments;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateArticle {
        private String title;
        private String subtitle;
        private String text;
        private Topic topic;
    }

}
