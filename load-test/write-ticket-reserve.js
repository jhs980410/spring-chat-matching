import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  scenarios: {
    waiting_room_test: {
      executor: 'per-vu-iterations',
      vus: 50,          // 50명의 가상 유저
      iterations: 1,    // 유저당 1회 실행
      maxDuration: '5m',
    },
  },
};

const BASE_URL = 'http://localhost:8080';
const EVENT_ID = 1;

export default function () {
  // ----------------------------------------------------------------
  // 0. 로그인 (서비스 로직에 맞춘 토큰 추출)
  // ----------------------------------------------------------------
  const loginUrl = `${BASE_URL}/api/auth/user/login`;
  const loginPayload = JSON.stringify({
    email: `test${__VU}@example.com`, // DB에 test1~50@example.com 존재 가정
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

  // 서비스 코드의 AuthResponse 필드명 'access'에 맞춰 추출
  const accessToken = loginRes.json().accessToken; 
  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${accessToken}`,
  };

  // ----------------------------------------------------------------
  // 1. 대기열 진입 (Join)
  // ----------------------------------------------------------------
  let joinRes = http.post(`${BASE_URL}/api/waiting-room/${EVENT_ID}/join`, null, { headers });
  check(joinRes, { '1. Joined Queue': (r) => r.status === 200 });

  // ----------------------------------------------------------------
  // 2. 대기열 폴링 (Polling - 1초당 2명씩 해소되는지 확인)
  // ----------------------------------------------------------------
  let isAvailable = false;
  let startTime = Date.now();

  while (!isAvailable) {
    let statusRes = http.get(`${BASE_URL}/api/waiting-room/${EVENT_ID}/status`, { headers });
    let body = statusRes.json();

    if (body.status === 'AVAILABLE') {
      isAvailable = true;
      let waitTime = (Date.now() - startTime) / 1000;
      console.log(`[VU ${__VU}] ★ 입장 성공! (대기시간: ${waitTime}초)`);
    } else {
      // 1초 간격으로 상태 체크
      sleep(1);
    }
    
    // 5분 초과 시 타임아웃
    if ((Date.now() - startTime) > 300000) break;
  }

  // ----------------------------------------------------------------
  // 3. 최종 주문 API (Access Pass가 있어야 통과 가능)
  // ----------------------------------------------------------------
  if (isAvailable) {
    const orderRes = http.post(
      `${BASE_URL}/api/orders`,
      JSON.stringify({ eventId: EVENT_ID, seatIds: [1] }), // 동일 좌석 경쟁
      { headers }
    );

    check(orderRes, {
      '2. Order Success or Conflict': (r) => r.status === 200 || r.status === 409,
    });
  }
}