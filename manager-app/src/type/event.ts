/** * 1. 티켓 초안 생성 요청 (TicketDraftCreateRequest) 
 * 기준
 */
export interface TicketDraftCreateRequest {
  name: string;           // 티켓명 (예: VIP석)
  price: number;          // 가격 (int32)
  totalQuantity: number;  // 총 수량 (int32)
  sectionCode: string;    // 구역 코드 (예: A1)
  sectionName: string;    // 구역 이름 (예: 1층 좌측)
  rowLabel: string;       // 열 정보 (예: A열)
}

/** * 2. 공연 초안 생성 요청 (EventDraftCreateRequest)
 * 기준 
 */
export interface EventDraftCreateRequest {
  domainId: number;       // 도메인 ID
  title: string;          // 공연 제목
  categoryId: number;     // 카테고리 ID (오늘 백엔드 트러블슈팅의 핵심)
  description: string;    // 공연 설명
  venue: string;          // 장소
  startAt: string;        // 시작 일시 (ISO 8601 String)
  endAt: string;          // 종료 일시 (ISO 8601 String)
  thumbnail: string;      // 썸네일 URL
}

/** * 3. 최종 Draft 생성 요청 Wrapper (CreateDraftRequest)
 *의 record 구조 기준
 */
export interface CreateDraftRequest {
  salesContractDraftId: number;         // 필수: 승인된 계약 ID
  event: EventDraftCreateRequest;       // 공연 정보
  tickets: TicketDraftCreateRequest[];  // 티켓 리스트 (최소 1개 이상)
}

/** * 4. Draft 목록 응답 (EventDraftResponse)
 */
export interface EventDraftResponse {
  id: number;
  status: 'DRAFT' | 'REQUESTED' | 'APPROVED' | 'REJECTED';
  title: string;
  updatedAt: string;
}