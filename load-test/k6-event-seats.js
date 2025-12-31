import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 50,
  duration: '2m',
};

export default function () {
  const eventId = 1;

  const res = http.get(
    `http://localhost:8080/api/events/${eventId}/seats`
  );

  check(res, {
    'status is 200': (r) => r.status === 200,
    'has sections': (r) => JSON.parse(r.body).length > 0,
  });

  sleep(0.5);
}
