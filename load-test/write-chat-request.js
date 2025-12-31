import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 30 },
    { duration: '30s', target: 60 },
    { duration: '30s', target: 100 },
    { duration: '30s', target: 30 },
  ],
};

const BASE_URL = 'http://localhost:8080';
const TOKEN = 'Bearer test-user-jwt-token'; // 더미

export default function () {
  const payload = JSON.stringify({
    domainId: 1,
    categoryId: 1,
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
      Authorization: TOKEN,
    },
  };

  const res = http.post(
    `${BASE_URL}/api/chat/request`,
    payload,
    params
  );

  check(res, {
    'status is 200': (r) => r.status === 200,
  });

  sleep(1);
}
