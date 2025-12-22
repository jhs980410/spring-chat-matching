package com.chatmatchingservice.springchatmatching.domain.ReserveUser.dto;

import com.chatmatchingservice.springchatmatching.domain.ReserveUser.entity.ReserveUser;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ReserveUserDetailDto {

    private Long id;
    private String realName;
    private String phone;
    private String email;
    private LocalDate birth;
    private String zipCode;
    private String address1;
    private String address2;
    private boolean isDefault;

    public static ReserveUserDetailDto from(ReserveUser entity) {
        return new ReserveUserDetailDto(
                entity.getId(),
                entity.getRealName(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getBirth(),
                entity.getZipCode(),
                entity.getAddress1(),
                entity.getAddress2(),
                entity.isDefault()
        );
    }
}
