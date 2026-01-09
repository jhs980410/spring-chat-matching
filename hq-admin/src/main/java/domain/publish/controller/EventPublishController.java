package domain.publish.controller;

import domain.publish.service.EventPublishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "HQ Publish",
        description = """
        승인 완료 Draft를 운영 DB로 반영하는 API
        """
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hq/publish")
public class EventPublishController {

    private final EventPublishService publishService;

    @Operation(summary = "Draft Publish")
    @PostMapping("/{draftId}")
    public ResponseEntity<Long> publish(
            @PathVariable Long draftId
    ) {
        return ResponseEntity.ok(
                publishService.publish(draftId)
        );
    }
}
