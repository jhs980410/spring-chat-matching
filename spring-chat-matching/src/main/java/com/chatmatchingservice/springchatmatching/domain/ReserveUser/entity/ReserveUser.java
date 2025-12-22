package com.chatmatchingservice.springchatmatching.domain.ReserveUser.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "reserve_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReserveUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "real_name", nullable = false, length = 50)
    private String realName;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 200)
    private String email;

    private LocalDate birth;

    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @Column(length = 200)
    private String address1;

    @Column(length = 200)
    private String address2;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public ReserveUser(
            Long userId,
            String realName,
            String phone,
            String email,
            LocalDate birth,
            String zipCode,
            String address1,
            String address2,
            boolean isDefault
    ) {
        this.userId = userId;
        this.realName = realName;
        this.phone = phone;
        this.email = email;
        this.birth = birth;
        this.zipCode = zipCode;
        this.address1 = address1;
        this.address2 = address2;
        this.isDefault = isDefault;
    }

    // =========================
    // 비즈니스 메서드
    // =========================

    public void updateInfo(
            String realName,
            String phone,
            String email,
            LocalDate birth,
            String zipCode,
            String address1,
            String address2
    ) {
        this.realName = realName;
        this.phone = phone;
        this.email = email;
        this.birth = birth;
        this.zipCode = zipCode;
        this.address1 = address1;
        this.address2 = address2;
    }

    public void markAsDefault() {
        this.isDefault = true;
    }

    public void unmarkAsDefault() {
        this.isDefault = false;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
