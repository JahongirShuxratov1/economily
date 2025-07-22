package org.example.economily.entity;

import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.compress.harmony.pack200.BandSet;
import org.example.economily.entity.BaseEntity;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "codes")
public class Code extends BaseEntity {
    private String code;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    @Column(name = "counts")
    private Integer counts;

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;
}

