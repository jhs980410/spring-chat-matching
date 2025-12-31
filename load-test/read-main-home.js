import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
   { duration: '30s', target: 550 }, 
    { duration: '30s', target: 650 }, // 500명 돌파 시도
    { duration: '30s', target: 750 }, // 700명까지 한계 테스트
    { duration: '30s', target: 0 },   // 안정적인 종료
  ],
  thresholds: {
    http_req_duration: ['p(95)<300'], // 95%의 응답은 0.3초 이내여야 함
    http_req_failed: ['rate<0.01'],   // 에러율은 1% 미만이어야 함
  },
};

export default function () {
  const res = http.get('http://localhost:8080/api/events/home');

  check(res, {
    'status is 200': (r) => r.status === 200,
  });

  // 각 요청 사이에 1초의 대기 시간을 두어 실제 사용자 패턴 모사
  sleep(1);
}