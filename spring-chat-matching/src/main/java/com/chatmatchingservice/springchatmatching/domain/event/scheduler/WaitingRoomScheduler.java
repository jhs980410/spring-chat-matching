package com.chatmatchingservice.springchatmatching.domain.event.scheduler;

import com.chatmatchingservice.springchatmatching.domain.event.service.WaitingRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WaitingRoomScheduler {

    private final WaitingRoomService waitingRoomService;

    /**
     * 대기열 유저를 주기적으로 입장시키는 심장부 로직
     * fixedDelay = 1000: 이전 작업이 종료된 시점부터 1초 뒤에 다시 실행 (작업 중첩 방지)
     */
    @Scheduled(fixedDelay = 1000)
    public void processWaitingQueue() {
        // 1. 실제 운영 환경이라면 DB나 캐시에서 '현재 대기열이 활성화된 이벤트 ID 목록'을 가져와야 합니다.
        // 우선은 테스트를 위해 현재 진행 중인 이벤트 ID 리스트를 가정합니다.
        List<Long> activeEventIds = List.of(1L, 2L); // 예: 아이유 콘서트(1L), 뮤지컬(2L)

        // 2. 한 번에 입장시킬 인원 설정 (서버 사양에 따라 10~100명 사이 조정)
        int entryCountPerSecond = 2;

        for (Long eventId : activeEventIds) {
            try {
                // 서비스의 allowEntry를 호출하여 50명씩 입장권 부여
                waitingRoomService.allowEntry(eventId, entryCountPerSecond);
            } catch (Exception e) {
                log.error("대기열 처리 중 장애 발생 - EventID: {}, Error: {}", eventId, e.getMessage());
            }
        }
    }
}