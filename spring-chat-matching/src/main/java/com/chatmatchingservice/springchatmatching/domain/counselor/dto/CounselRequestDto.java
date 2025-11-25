package com.chatmatchingservice.springchatmatching.domain.counselor.dto;

public record CounselRequestDto(
        Long userId,
        Long categoryId
//        String content
) {}