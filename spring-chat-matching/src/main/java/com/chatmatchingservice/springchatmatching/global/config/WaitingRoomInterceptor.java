//package com.chatmatchingservice.springchatmatching.global.config;
//
//import com.chatmatchingservice.springchatmatching.domain.event.service.WaitingRoomService;
//import com.chatmatchingservice.springchatmatching.global.error.CustomException;
//import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.HandlerMapping;
//
//import java.util.Map;
//
//@Component
//@RequiredArgsConstructor
//public class WaitingRoomInterceptor implements HandlerInterceptor {
//
//    private final WaitingRoomService waitingRoomService;
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//
//        Long eventId = extractEventId(request);
//
//        if (eventId == null) {
//            throw new CustomException(ErrorCode.BAD_REQUEST);
//        }
//
//        // 2. SecurityContext에서 userId 추출
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth == null || !auth.isAuthenticated()) {
//            throw new CustomException(ErrorCode.UNAUTHORIZED);
//        }
//        Long userId = (Long) auth.getPrincipal();
//
//        // 3. Redis 입장권 확인
//        if (!waitingRoomService.canAccess(eventId, userId)) {
//            throw new CustomException(ErrorCode.UNAUTHORIZED);
//        }
//
//        return true;
//    }
//
//    private Long extractEventId(HttpServletRequest request) {
//        // 1순위: URL 경로 변수 확인 (예: /api/events/{eventId}/seats)
//        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
//        if (pathVariables != null && pathVariables.containsKey("eventId")) {
//            return Long.parseLong(pathVariables.get("eventId"));
//        }
//
//        // 2순위: 쿼리 파라미터 확인 (예: /api/orders?eventId=1)
//        String paramId = request.getParameter("eventId");
//        if (paramId != null) {
//            return Long.parseLong(paramId);
//        }
//
//        return null;
//    }
//}