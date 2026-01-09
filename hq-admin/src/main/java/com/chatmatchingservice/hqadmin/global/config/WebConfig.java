package com.chatmatchingservice.hqadmin.global.config;//package com.chatmatchingservice.springchatmatching.global.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//@RequiredArgsConstructor
//public class WebConfig implements WebMvcConfigurer {
//     // 인터셉터 적용예정
//    private final WaitingRoomInterceptor waitingRoomInterceptor;
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(waitingRoomInterceptor)
//
//                // 1. 좌석 조회 (예매창 진입 시점)
//                .addPathPatterns("/api/events/*/seats")
//
//                // 2. 주문 생성 및 좌석 선점 (결제 시도 시점)
//                .addPathPatterns("/api/orders/**")
//
//                // 대기열 관련 API는 검사에서 제외 (무한 루프 방지)
//                .excludePathPatterns("/api/waiting-room/**");
//    }
//}