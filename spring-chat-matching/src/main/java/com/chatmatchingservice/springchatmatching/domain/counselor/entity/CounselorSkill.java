package com.chatmatchingservice.springchatmatching.domain.counselor.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "counselor_skill",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_counselor_skill", columnNames = {"counselor_id", "category_id"})
        })
public class CounselorSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "counselor_id", nullable = false)
    private Long counselorId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public CounselorSkill(Long counselorId, Long categoryId) {
        this.counselorId = counselorId;
        this.categoryId = categoryId;
        this.createdAt = LocalDateTime.now();
    }
}
