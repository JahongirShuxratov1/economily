package org.example.economily.repository;

import org.example.economily.entity.Article;
import org.example.economily.entity.Like;
import org.example.economily.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like,Long> {
    boolean existsByArticleAndUser(Article article, User user);

    Optional<Like> findByArticleAndUser(Article article, User user);
}
