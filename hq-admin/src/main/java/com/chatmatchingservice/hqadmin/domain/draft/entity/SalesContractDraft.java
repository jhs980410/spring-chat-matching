package com.chatmatchingservice.hqadmin.domain.draft.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
/**
 * [최종 수정]
 * 1. 백틱(`)을 완전히 제거하세요.
 * 2. name에는 테이블명만, schema에는 스키마명만 넣습니다.
 * 이렇게 해야 SQL 실행 시 `ticket_manager`.`sales_contract_draft`로 점(.)이 분리됩니다.
 */
@Table(name = "sales_contract_draft", catalog = "ticket_manager")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SalesContractDraft {

    @Id
    /**
     * 8081(Manager)에서 이미 생성된 ID(예: 4번)를 조회해야 하므로
     * 자동 생성 전략(@GeneratedValue)을 사용하지 않습니다.
     */
    private Long id;

    @Enumerated(EnumType.STRING)
    private DraftStatus status;

    public void approve() {
        this.status = DraftStatus.APPROVED;
    }
}