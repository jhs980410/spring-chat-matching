import http from 'k6/http';
import { check, sleep } from 'k6';

// 1. 테스트 부하 설정: 100명이 동시에 1번씩 실행
export const options = {
  scenarios: {
    seat_lock_battle: {
      executor: 'per-vu-iterations',
      vus: 100,      // 100명의 가상 유저
      iterations: 1, // 유저당 1번의 시퀀스
    },
  },
};

export default function () {
  // 실제 로그인하여 발급받은 최신 토큰을 입력하세요.
  const token = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3NjczMjg4MTgsImV4cCI6MTc2NzMzMDYxOH0.YdfH4oCPS3K0GGto7DpuZdVEqe9s4FOY3p5EQI-xhcPg4ZrQimDX8qU48pPWr_yjgVqqtmOiWoeNhj0o1BrREv0dMiQ12jwcbcDzJRqqchsjmnzCLprbPtBKnL1aqqfP8gSTMnZh9k_HDIBq8gh8_-WAebHcyUhtuTL7ItFO3v698xausL_P79Z8-ZQbnkqa3UUsjh3LPgQKnpGpG6XLN8gs9Q0E2z1dVgWIds-jzyMa5jT05Y5xaoQJh1hIqYPKnMg4tiMspACLdXyXaivUPWA6eqC4V8L3Sp9r7-LLpPjhwFqJO7NEszPPzkd0Ru0-NXDEe8mMA2dWy0LUuXo2zg"; 
  const baseUrl = 'http://13.209.214.254/api/orders';
  
  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
  };

  // Step 1: 주문 생성 (PENDING 단계)
  // 실제 DB에 존재하는 eventId: 1, seatIds: [1]을 사용합니다.
  const orderPayload = JSON.stringify({ eventId: 1, seatIds: [1] });
  const orderRes = http.post(baseUrl, orderPayload, params);
  
  // 주문 생성이 실패하면 로직 중단 (데이터/인증 문제 확인용)
  if (orderRes.status !== 200) {
    console.log(`주문 생성 실패: ${orderRes.status} - ${orderRes.body}`);
    return;
  }

  // 서버 응답 DTO 필드 확인 (id 또는 orderId)
  const orderId = orderRes.json().id;

  // Step 2: 좌석 선점 경쟁 (Redis Lock 단계)
  // 모든 유저가 동일한 1번 좌석을 선점하려고 시도합니다.
  const reserveUrl = `${baseUrl}/${orderId}/reserve`;
  const reservePayload = JSON.stringify({ eventId: 1, seatIds: [1] }); 
  const reserveRes = http.post(reserveUrl, reservePayload, params);

  // 결과 검증
  check(reserveRes, {
    'Success (단 1명만 성공)': (r) => r.status === 200,
    '이선좌 (99명은 실패)': (r) => r.status === 500 || r.status === 400,
  });

  // 모든 유저가 동시에 몰리도록 짧게 대기
  sleep(0.1);
}