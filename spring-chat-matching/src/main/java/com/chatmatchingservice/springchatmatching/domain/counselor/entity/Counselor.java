package com.chatmatchingservice.springchatmatching.domain.counselor.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Counselor {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 200)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    private CounselorStatus status = CounselorStatus.OFFLINE;

    private int currentLoad = 0;

    private LocalDateTime lastFinishedAt;

    private LocalDateTime createdAt = LocalDateTime.now();
}
