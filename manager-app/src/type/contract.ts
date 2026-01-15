/**
 * 1. 계약 상태 Enum
 * 스웨거 Enum 정의 기준
 */
export type ContractStatus = 'DRAFT' | 'REQUESTED' | 'APPROVED' | 'REJECTED';

/**
 * 2. 판매 계약 생성 요청 (Request)
 * SalesContractDraftCreateRequest 기준
 */
export interface SalesContractDraftCreateRequest {
  partnerDraftId: number;   // 파트너 초안 ID
  domainId: number;         // 도메인 ID
  businessName: string;     // 사업자명
  businessNumber: string;   // 사업자번호
  ceoName: string;          // 대표자명
  contactEmail: string;     // 담당자 이메일
  contactPhone: string;     // 담당자 전화번호
  settlementEmail: string;  // 정산용 이메일
  salesReportEmail: string; // 매출 보고용 이메일
  taxEmail: string;         // 세금계산서 이메일
  issueMethod: 'ONLINE' | 'ON_SITE' | 'DELIVERY'; // 발권 방식
}

/**
 * 3. 내 계약 목록 조회 응답 (Response)
 * SalesContractDraftResponse 기준
 */
export interface SalesContractDraftResponse {
  id: number;               // 계약 ID
  businessName: string;     // 사업자명
  status: ContractStatus;   // 현재 상태
  createdAt: string;        // 생성일 (ISO Date String)
  requestedAt: string;      // 승인 요청일 (ISO Date String)
}