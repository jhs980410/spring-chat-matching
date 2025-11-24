package domain.counselor.dto;

import domain.counselor.entity.CounselorStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CounselorStatusUpdateRequest {
    private Long counselorId;
    private CounselorStatus status;
    private Long categoryId;
}