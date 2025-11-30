package com.chatmatchingservice.springchatmatching.domain.counselor.dto;

public record CounselorSkillResponse(
        Long id,
        Long counselorId,
        Long categoryId
) { }
