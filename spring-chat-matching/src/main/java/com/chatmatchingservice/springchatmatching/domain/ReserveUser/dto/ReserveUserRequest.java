package com.chatmatchingservice.springchatmatching.domain.ReserveUser.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ReserveUserRequest {

    @NotBlank
    private String realName;

    @NotBlank
    private String phone;

    private String email;
    private LocalDate birth;

    private String zipCode;
    private String address1;
    private String address2;

    private boolean isDefault;
}
