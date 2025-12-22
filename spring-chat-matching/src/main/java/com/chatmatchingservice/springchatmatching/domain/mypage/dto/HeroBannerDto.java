package com.chatmatchingservice.springchatmatching.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class HeroBannerDto {

    private Long id;
    private String title;
    private String subtitle;
    private String imageUrl;

    public static HeroBannerDto of(Long id, String title, String subtitle, String imageUrl) {
        return HeroBannerDto.builder()
                .id(id)
                .title(title)
                .subtitle(subtitle)
                .imageUrl(imageUrl)
                .build();
    }
}
