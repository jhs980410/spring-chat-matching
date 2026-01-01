import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 300 }, 
    { duration: '30s', target: 500 }, 
    { duration: '30s', target: 700 }, 
    { duration: '30s', target: 1000 },   

  ],
  thresholds: {
    http_req_duration: ['p(95)<300'], 
    http_req_failed: ['rate<0.01'],   
  },
};

export default function () {
  // localhost 대신 EC2 서버의 탄력적 IP(EIP) 또는 도메인 주소를 입력하세요.
  // Nginx 프록시를 통해 80번 포트로 열려있다면 :8080을 떼고 호출해야 할 수도 있습니다.
const res = http.get('http://13.209.214.254/api/events/home'); 

  check(res, {
    'status is 200': (r) => r.status === 200,
  });

  sleep(1);
}