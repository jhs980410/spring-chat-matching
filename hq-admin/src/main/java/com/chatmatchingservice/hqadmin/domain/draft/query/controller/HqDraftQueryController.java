package com.chatmatchingservice.hqadmin.domain.draft.query.controller;


import com.chatmatchingservice.hqadmin.domain.draft.query.dto.EventDraftDetailResponse;
import com.chatmatchingservice.hqadmin.domain.draft.query.dto.EventDraftSummaryResponse;
import com.chatmatchingservice.hqadmin.domain.draft.query.service.HqDraftQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(
        name = "HQ Draft Read",
        description = "본사 어드민 Draft 조회 전용 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hq/drafts")
public class HqDraftQueryController {

    private final HqDraftQueryService service;

    @GetMapping
    public List<EventDraftSummaryResponse> getRequestedDrafts() {
        return service.getRequestedDrafts();
    }

    @GetMapping("/{id}")
    public EventDraftDetailResponse getDraftDetail(@PathVariable Long id) {
        return service.getDraftDetail(id);
    }
}
