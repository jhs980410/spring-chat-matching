package com.chatmatchingservice.hqadmin.domain.draft.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * [수정 포인트]
 * 1. catalog = "ticket_manager" 추가하여 스키마 경로 명시
 * 2. 8081의 ticket_draft 테이블과 매핑
 */

@Entity
@Table(name = "ticket_draft", catalog = "ticket_manager")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketDraftEntity {

    @Id
    private Long id;

    @Column(name = "event_draft_id")
    private Long eventDraftId;

    private String name;
    private int price;

    @Column(name = "total_quantity")
    private int totalQuantity;

    // 💡 [추가] 매니저가 입력한 구역 및 행 정보를 읽어오기 위한 필드
    @Column(name = "section_code")
    private String sectionCode;

    @Column(name = "section_name")
    private String sectionName;

    @Column(name = "row_label")
    private String rowLabel;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}