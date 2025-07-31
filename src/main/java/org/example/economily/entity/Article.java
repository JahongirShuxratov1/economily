package org.example.economily.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.economily.enums.Topic;
import org.hibernate.annotations.Fetch;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "article")
public class Article extends BaseEntity{
    private String title;

    private String subtitle;

    @Column(length = 120000)
    private String text;

    @Enumerated(EnumType.STRING)
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User createdBy;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Like> likes;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Comment> comments;

}
