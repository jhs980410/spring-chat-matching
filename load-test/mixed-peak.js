import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
  scenarios: {
    read_home: {
      executor: 'constant-arrival-rate',
      rate: 70,
      timeUnit: '1s',
      duration: '3m',
      preAllocatedVUs: 100,
      exec: 'home',
    },
    chat_request: {
      executor: 'constant-arrival-rate',
      rate: 20,
      timeUnit: '1s',
      duration: '3m',
      preAllocatedVUs: 50,
      exec: 'chat',
    },
    ticket_reserve: {
      executor: 'constant-arrival-rate',
      rate: 10,
      timeUnit: '1s',
      duration: '3m',
      preAllocatedVUs: 30,
      exec: 'ticket',
    },
  },
};

export function home() {
  http.get('http://localhost:8080/api/events/home');
  sleep(1);
}

export function chat() {
  http.post(
    'http://localhost:8080/api/chat/request',
    JSON.stringify({ domainId: 1, categoryId: 1 }),
    { headers: { 'Content-Type': 'application/json' } }
  );
  sleep(1);
}

export function ticket() {
  http.get('http://localhost:8080/api/events/1/seats');
  sleep(1);
}
