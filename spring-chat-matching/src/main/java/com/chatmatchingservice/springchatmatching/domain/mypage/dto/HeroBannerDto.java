package com.chatmatchingservice.springchatmatching.domain.mypage.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
