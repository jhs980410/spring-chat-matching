package com.chatmatchingservice.springchatmatching.domain.ReserveUser.service;

import com.chatmatchingservice.springchatmatching.domain.ReserveUser.dto.ReserveUserDetailDto;
import com.chatmatchingservice.springchatmatching.domain.ReserveUser.dto.ReserveUserRequest;
import com.chatmatchingservice.springchatmatching.domain.ReserveUser.dto.ReserveUserSummaryDto;
import com.chatmatchingservice.springchatmatching.domain.ReserveUser.entity.ReserveUser;
import com.chatmatchingservice.springchatmatching.domain.ReserveUser.repository.ReserveUserRepository;
import com.chatmatchingservice.springchatmatching.global.error.BusinessException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class ReserveUserService {

    private final ReserveUserRepository reserveUserRepository;

    /* =========================
       목록 조회
    ========================= */
    @Transactional(readOnly = true)
    public List<ReserveUserSummaryDto> getMyReserveUsers(Long userId) {
        return reserveUserRepository.findAllByUserIdOrderByIdDesc(userId)
                .stream()
                .map(ReserveUserSummaryDto::from)
                .toList();
    }

    /* =========================
       상세 조회
    ========================= */
    @Transactional(readOnly = true)
    public ReserveUserDetailDto getReserveUser(Long userId, Long reserveUserId) {
        ReserveUser reserveUser = reserveUserRepository
                .findByIdAndUserId(reserveUserId, userId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.RESERVE_USER_NOT_FOUND)
                );

        return ReserveUserDetailDto.from(reserveUser);
    }

    /* =========================
       등록
    ========================= */
    public Long createReserveUser(Long userId, ReserveUserRequest request) {

        if (request.isDefault()) {
            reserveUserRepository.clearDefaultReserveUser(userId);
        }

        ReserveUser reserveUser = ReserveUser.builder()
                .userId(userId)
                .realName(request.getRealName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .birth(request.getBirth())
                .zipCode(request.getZipCode())
                .address1(request.getAddress1())
                .address2(request.getAddress2())
                .isDefault(request.isDefault())
                .build();

        reserveUserRepository.save(reserveUser);

        // ⭐ 최초 예매자면 자동 default
        if (!reserveUserRepository.existsByUserId(userId)) {
            reserveUser.markAsDefault();
        }

        return reserveUser.getId();
    }

    /* =========================
       수정
    ========================= */
    public void updateReserveUser(Long userId, Long reserveUserId, ReserveUserRequest request) {

        ReserveUser reserveUser = reserveUserRepository
                .findByIdAndUserId(reserveUserId, userId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.RESERVE_USER_NOT_FOUND)
                );

        if (request.isDefault()) {
            reserveUserRepository.clearDefaultReserveUser(userId);
        }

        reserveUser.updateInfo(
                request.getRealName(),
                request.getPhone(),
                request.getEmail(),
                request.getBirth(),
                request.getZipCode(),
                request.getAddress1(),
                request.getAddress2()
        );

        if (request.isDefault()) {
            reserveUser.markAsDefault();
        }
    }

    /* =========================
       삭제
    ========================= */
    public void deleteReserveUser(Long userId, Long reserveUserId) {

        ReserveUser reserveUser = reserveUserRepository
                .findByIdAndUserId(reserveUserId, userId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.RESERVE_USER_NOT_FOUND)
                );

        if (reserveUserRepository.existsOrderByReserveUserId(reserveUserId)) {
            throw new BusinessException(ErrorCode.RESERVE_USER_ALREADY_USED);
        }

        boolean wasDefault = reserveUser.isDefault();
        reserveUserRepository.delete(reserveUser);

        // ⭐ default 삭제 보정
        if (wasDefault) {
            reserveUserRepository
                    .findFirstByUserIdOrderByIdAsc(userId)
                    .ifPresent(ReserveUser::markAsDefault);
        }
    }
}
