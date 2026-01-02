package com.chatmatchingservice.springchatmatching.global.config;

import com.chatmatchingservice.springchatmatching.domain.event.service.WaitingRoomService;
import com.chatmatchingservice.springchatmatching.global.error.CustomException;
import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class WaitingRoomInterceptor implements HandlerInterceptor {

    private final WaitingRoomService waitingRoomService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. 요청에서 eventId와 userId 추출 (예시)
        Long eventId = Long.parseLong(request.getParameter("eventId"));
        Long userId = (Long) request.getAttribute("userId");

        // 2. 서비스의 canAccess를 호출하여 '입장권'이 있는지 최종 확인
        if (!waitingRoomService.canAccess(eventId, userId)) {
            // 입장권이 없으면 예외를 던져서 실행 차단
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return true; // 입장권이 있으면 통과
    }
}