import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 20,
  iterations: 5,
};

const BASE_URL = 'http://localhost:8080';
const TOKEN = 'Bearer test-user-jwt-token';

export default function () {
  // 1️⃣ 주문 생성
  const orderRes = http.post(
    `${BASE_URL}/api/orders`,
    JSON.stringify({
      eventId: 1,
      reserveUserId: 1,
      totalPrice: 150000,
    }),
    {
      headers: {
        'Content-Type': 'application/json',
        Authorization: TOKEN,
      },
    }
  );

  check(orderRes, {
    'order created': (r) => r.status === 200,
  });

  const orderId = JSON.parse(orderRes.body).orderId;

  // 2️⃣ 좌석 락
  const reserveRes = http.post(
    `${BASE_URL}/api/orders/${orderId}/reserve`,
    JSON.stringify({
      eventId: 1,
      seatIds: [1, 2],
    }),
    {
      headers: {
        'Content-Type': 'application/json',
      },
    }
  );

  check(reserveRes, {
    'seat reserved': (r) => r.status === 200,
  });

  sleep(1);
}
