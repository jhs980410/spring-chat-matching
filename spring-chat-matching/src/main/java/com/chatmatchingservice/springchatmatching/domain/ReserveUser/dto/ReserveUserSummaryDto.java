package com.chatmatchingservice.springchatmatching.domain.ReserveUser.dto;

import com.chatmatchingservice.springchatmatching.domain.ReserveUser.entity.ReserveUser;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReserveUserSummaryDto {

    private Long id;
    private String realName;
    private String phone;
    private boolean isDefault;

    public static ReserveUserSummaryDto from(ReserveUser entity) {
        return new ReserveUserSummaryDto(
                entity.getId(),
                entity.getRealName(),
                entity.getPhone(),
                entity.isDefault()
        );
    }
}

