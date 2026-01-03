package com.chatmatchingservice.springchatmatching.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public enum ErrorCode {

    // ======== 공통 ========
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_001", "잘못된 입력 값입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_002", "인증이 필요합니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_002", "잘못된 요청입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_003", "접근 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_004", "리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_999", "서버 내부 오류가 발생했습니다."),


    // ======== 인증 / 회원가입 / 로그인 ========
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "AUTH_001", "이미 사용 중인 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "AUTH_002", "이미 사용 중인 닉네임입니다."),

    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH_003", "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "AUTH_004", "올바르지 않은 비밀번호 형식입니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "AUTH_005", "올바르지 않은 이메일 형식입니다."),
    ACCOUNT_BLOCKED(HttpStatus.FORBIDDEN, "AUTH_006", "차단된 계정입니다."),

    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_007", "토큰이 만료되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH_008", "유효하지 않은 토큰입니다."),
    TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTH_009", "토큰이 필요합니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_010", "유효하지 않은 리프레시 토큰입니다."),


    // ======== 상담사 ========
    COUNSELOR_NOT_FOUND(HttpStatus.NOT_FOUND, "COUNSELOR_001", "상담사를 찾을 수 없습니다."),
    COUNSELOR_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "COUNSELOR_002", "상담사가 상담 가능한 상태가 아닙니다."),

    // ======== 예매자 (ReserveUser) ========
    RESERVE_USER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "RESERVE_USER_001",
            "예매자 정보를 찾을 수 없습니다."
    ),

    RESERVE_USER_ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "RESERVE_USER_002",
            "해당 예매자 정보에 접근할 수 없습니다."
    ),

    RESERVE_USER_ALREADY_USED(
            HttpStatus.BAD_REQUEST,
            "RESERVE_USER_003",
            "주문에 사용된 예매자는 삭제할 수 없습니다."
    ),

    RESERVE_USER_DEFAULT_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "RESERVE_USER_004",
            "기본 예매자는 최소 1명 이상 존재해야 합니다."
    ),
    // ======== 주문 (Order) ========
    ORDER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "ORDER_001",
            "주문을 찾을 수 없습니다."
    ),

    ORDER_ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "ORDER_002",
            "해당 주문에 접근할 수 없습니다."
    ),

    ORDER_INVALID_STATUS(
            HttpStatus.BAD_REQUEST,
            "ORDER_003",
            "주문 상태가 올바르지 않습니다."
    ),

    ORDER_ALREADY_PAID(
            HttpStatus.CONFLICT,
            "ORDER_004",
            "이미 결제가 완료된 주문입니다."
    ),

    // ======== 세션 ========
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "SESSION_001", "세션을 찾을 수 없습니다."),
    SESSION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "SESSION_002", "이 세션에 접근할 수 없습니다."),
    SESSION_ALREADY_EXISTS(HttpStatus.CONFLICT, "SESSION_003", "이미 활성화된 세션이 있습니다."),
    SESSION_ALREADY_FINISHED(HttpStatus.BAD_REQUEST, "SESSION_004", "이미 종료된 세션입니다."),
    SESSION_NOT_ASSIGNED(HttpStatus.BAD_REQUEST, "SESSION_005", "상담사가 배정되지 않은 세션입니다."),


    // ======== 매칭 ========
    NO_WAITING_USER(HttpStatus.NOT_FOUND, "MATCH_001", "대기 중인 유저가 없습니다."),
    NO_AVAILABLE_COUNSELOR(HttpStatus.NOT_FOUND, "MATCH_002", "대기 중인 상담사가 없습니다."),
    MATCHING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MATCH_999", "매칭 처리 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
