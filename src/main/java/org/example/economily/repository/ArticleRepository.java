package org.example.economily.repository;

import org.example.economily.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ArticleRepository extends JpaRepository<Article,Long> {
    @Query(value = "select * from article a where (:topicName is null or a.topic =:topicName)",nativeQuery = true)
    Page<Article> findAll(String topicName, Pageable pageable);
}
