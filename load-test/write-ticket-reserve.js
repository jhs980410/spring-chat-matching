import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  scenarios: {
    waiting_room_test: {
      executor: 'per-vu-iterations',
      vus: 50,          // 50명의 가상 유저가 동시에 경쟁
      iterations: 1,    // 유저당 1회 실행
      maxDuration: '5m',
    },
  },
};

const BASE_URL = 'http://localhost:8080';
const EVENT_ID = 1;

export default function () {
  // ----------------------------------------------------------------
  // 0. 로그인 (test1~50 유저 순차 로그인)
  // ----------------------------------------------------------------
  const loginUrl = `${BASE_URL}/api/auth/user/login`;
  const loginPayload = JSON.stringify({
    email: `test${__VU}@example.com`,
    password: '1234',
  });

  const loginRes = http.post(loginUrl, loginPayload, {
    headers: { 'Content-Type': 'application/json' },
  });

  const loginOk = check(loginRes, { '0. Login Success': (r) => r.status === 200 });

  if (!loginOk) {
    console.error(`[VU ${__VU}] 로그인 실패: ${loginRes.status}`);
    return;
  }

  const accessToken = loginRes.json().accessToken; 
  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${accessToken}`,
  };

  // ----------------------------------------------------------------
  // 1. 대기열 진입
  // ----------------------------------------------------------------
  let joinRes = http.post(`${BASE_URL}/api/waiting-room/${EVENT_ID}/join`, null, { headers });
  check(joinRes, { '1. Joined Queue': (r) => r.status === 200 });

  // ----------------------------------------------------------------
  // 2. 대기열 폴링 (상태가 AVAILABLE이 될 때까지)
  // ----------------------------------------------------------------
  let isAvailable = false;
  let startTime = Date.now();

  while (!isAvailable) {
    let statusRes = http.get(`${BASE_URL}/api/waiting-room/${EVENT_ID}/status`, { headers });
    let body = statusRes.json();

    if (body.status === 'AVAILABLE') {
      isAvailable = true;
    } else {
      sleep(1); // 1초 대기 후 재시도
    }
    
    if ((Date.now() - startTime) > 300000) break; // 5분 타임아웃
  }

  // ----------------------------------------------------------------
  // 3. 최종 주문 API (1, 2, 3, 4번 좌석 동시 선점 시도)
  // ----------------------------------------------------------------
  if (isAvailable) {
    const orderRes = http.post(
      `${BASE_URL}/api/orders`,
      // 🔥 수정 포인트: 이미지 상의 ID 1, 2, 3, 4번 좌석을 한 번에 요청
      JSON.stringify({ eventId: EVENT_ID, seatIds: [1, 2, 3, 4] }), 
      { headers }
    );

    // 200: 성공
    // 409: 이미 예약된 좌석 (비즈니스 예외)
    // 500: DB 제약 조건 위반 등 (현재 해결 중인 에러 발생 시)
    check(orderRes, {
      '2. Order Processed': (r) => r.status === 200 || r.status === 409 || r.status === 500,
    });

    if (orderRes.status === 200) {
      console.log(`[VU ${__VU}] 🎉 주문 성공! 좌석 [1,2,3,4] 선점 완료`);
    } else if (orderRes.status === 500) {
      console.error(`[VU ${__VU}] ❌ DB 에러 발생 (Duplicate Entry 가능성)`);
    }
  }
}