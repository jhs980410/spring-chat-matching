# 🎟️ Ticket-Live: 실시간 예매 및 CS 상담 통합 플랫폼

> **"Redis 분산 락을 통한 고성능 동시성 제어와 상담사 상태 기반 매칭 엔진을 결합한 실전형 백엔드 서비스"**
> 
> 본 서비스는 **단일 AWS EC2 (Free Tier)** 환경에서 자원을 최적화하여 설계되었습니다. Redis를 활용해 실시간성(매칭, 좌석 락)을 확보하고, MySQL로 비즈니스 핵심 데이터의 영속성을 보장합니다. REST API는 상태 조회에, WebSocket(STOMP)은 실시간 상호작용에 사용하도록 분리 설계되었습니다.

---
---
##  프로젝트 개요

### 1) 개발 정보 📅
* **개발 기간**: 2025.11.23 ~ 2026.01.02 (6주)
* **프로젝트 성격**: 1인 풀스택 개발 (Back-end 중심)
* **핵심 목표**: 대규모 트래픽 상황에서의 데이터 정합성 보장 및 실시간 매칭 로직 구현

### 2) 개발 환경 및 기술 스택 🛠️
| Backend | Frontend | Infra & DevOps | Tools & Design |
| :--- | :--- | :--- | :--- |
| Java 17 | React 19 (Vite) | **AWS EC2 (T3.Micro)** | **IntelliJ IDEA** |
| Spring Boot 3.2.4 | TypeScript | **Nginx (Reverse Proxy)** | **MySQL Workbench** |
| Spring Data JPA | Zustand | **Docker / Docker Compose** | **diagrams.net** |
| Redisson (Redis) | Mantine UI | GitHub Actions (CI/CD 구축 중) | GitHub |
| JJWT 0.12.5 | React Query / Axios | k6 (Load Testing) | Notion |
| MySQL | StompJS / SockJS | SpringDoc OpenAPI 3 | |
---


## 🏗️ 시스템 아키텍처 (System Architecture)

![시스템 아키텍처](img/시스템아키텍처.png)

* **Infrastructure**: AWS EC2 T3.Micro (Ubuntu 22.04 LTS)
* **Storage Strategy**: 
    * **Redis**: 실시간 데이터(상담 대기열), 동시성 제어(분산 락), STOMP 메시지 브로커
    * **MySQL**: 비즈니스 핵심 데이터(회원, 공연, 주문) 및 상담 통계 저장
* **Frontend**: React (Vite) - 유저용/상담사용 독립 서비스 운영

---

## ⚡ 핵심 기술적 도전 (Key Challenges)

### 1. 티켓 예매: Redisson 분산 락 기반 동시성 제어
인기 공연 예매 시 발생하는 중복 예매(Race Condition)를 방지하기 위해 **'선 점유 후 확정'** 전략을 도입했습니다.

#### [진입 제어 및 대기열 관리]
![진입 제어](img/티켓팅1.png)

* **대기열 기반 트래픽 제어**: `WaitingRoomScheduler`가 1초마다 유입량을 조절하며 유효한 유저에게 `AccessPass`를 부여합니다.
* **계층적 검증**: 컨트롤러 도입부에서 `hasAccessPass`를 직접 호출하여 대기열을 통과하지 않은 비정상 접근을 차단합니다.

#### [좌석 선점 및 원자적 처리]
![좌석 선점](img/티켓팅2%20.png)

* **Redisson MultiLock**: 여러 좌석 선택 시 원자적 단위로 락을 획득하여 부분 성공으로 인한 정합성 오류를 방지합니다.
* **All-or-Nothing 락**: `SeatLockService`가 Redis `SETNX`를 활용해 10분간 좌석을 점유하며, 하나라도 실패 시 전체 락을 즉시 해제(Rollback)합니다.

### 2. 채팅/매칭 엔진: Redis 기반 실시간 상담 배정
단순 1:1 채팅을 넘어 실제 CS 센터 운영 로직을 반영한 **상담사 상태 관리 및 매칭 시스템**을 구축했습니다.

#### [매칭 트리거 설계 최적화]
![매칭 트리거](img/고객트리거vs상담사트리거.png)

* **상담사 중심 트리거**: 유저 요청마다 매칭을 시도하는 불필요한 비용을 줄이기 위해, 상담사가 `READY` 상태가 되는 시점에 단일 매칭을 시도하는 효율적 설계를 채택했습니다.

#### [매칭 및 상담 프로세스]
![채팅 흐름](img/채팅.png)

* **상담사 라이프사이클**: `ONLINE` -> `READY` -> `BUSY` -> `AFTER_CALL` 상태를 Redis로 관리하여 실시간성을 확보했습니다.
* **공통 종료 템플릿**: `EndSessionTemplate`을 통해 상담 종료 시 부하(Load) 감소, 로그 저장, Redis 키 삭제를 일관되게 수행합니다.

---

## 💳 결제 정합성 및 보안 (Safe Payment)
데이터 변조 및 부정 결제를 방지하기 위해 **토스페이먼츠(Toss Payments)** 연동 시 3중 검증을 수행합니다.

1. **소유권 검증**: 결제 승인 요청자와 주문서 생성자의 일치 여부 확인.
2. **금액 교차 대조**: DB의 주문 금액과 외부 결제 API의 승인 금액을 실시간 비교.
3. **멱등성 확보**: 결제 키 중복 체크 및 주문 상태 확인으로 중복 승인을 원천 차단.

---

## 📊 데이터베이스 설계 (ERD)

![ERD](img/erd%20.png)

* **조회 최적화**: 좌석 상태 조회 시 N+1 문제를 방지하기 위해 Java Stream의 `groupingBy`를 사용하여 메모리 내에서 데이터 가공 처리.
* **통계 자동화**: 상담 종료 시 로그 데이터를 분석하여 상담사의 일일 KPI(평균 상담 시간, 처리 건수 등)를 자동으로 산출합니다.

---

## 🚀 성능 검증 (k6 Load Test)
100명의 가상 유저(VU)가 단일 좌석을 두고 동시 예매를 시도하는 상황을 테스트했습니다.

| 지표 | 결과 | 비고 |
| :--- | :--- | :--- |
| **HTTP 실패율** | 50% | 락 획득 실패 인원 정확히 차단 |
| **최종 예매 성공** | **단 1명** | 데이터 정합성 100% 보장 |
| **매칭 대기열** | 초당 2명 진입 | 프리티어 환경에서도 안정적 응답 |

---

## 📂 주요 API 목록

### 🔹 예매 & 결제 (REST)
* `POST /api/orders` : 임시 주문서 생성 (PENDING)
* `POST /api/orders/{orderId}/reserve` : 좌석 분산 락 획득 및 선점
* `POST /api/payments/confirm` : 토스 결제 승인 및 3중 검증

### 🔹 실시간 상담 (STOMP)
* `PUB /pub/match/request` : 유저 상담 매칭 요청 (Redis 대기열 인입)
* `SUB /sub/session/{id}` : 1:1 채팅 세션 실시간 메시징
* `PATCH /api/counselors/ready` : 상담사의 상태를 READY로 전환 (매칭 트리거)
