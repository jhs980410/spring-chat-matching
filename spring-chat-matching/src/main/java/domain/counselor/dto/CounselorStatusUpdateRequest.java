package domain.counselor.dto;

import domain.counselor.entity.CounselorStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CounselorStatusUpdateRequest {
    private CounselorStatus status; // ONLINE / AFTER_CALL / OFFLINE
    private Long categoryId;        // 어느 카테고리 대기열에 참여하는지 (필요 시)
}