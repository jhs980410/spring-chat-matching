import { Routes, Route, Navigate } from 'react-router-dom';
import { AdminLayout } from './components/layout/AdminLayout';
import { HqApprovalPage } from './pages/hq/HqApprovalPage';
import { HqPublishPage } from './pages/hq/HqPublishPage'; // 추가

function App() {
  return (
    <Routes>
      {/* HQ 관리자 경로 설정 */}
      <Route path="/hq" element={<AdminLayout />}>
        {/* 기본 접속 시 승인 대기 목록으로 이동 */}
        <Route index element={<Navigate to="/hq/approvals" replace />} />
        
        {/* 1. 승인 관리 페이지 (검토/반려/승인) */}
        <Route path="approvals" element={<HqApprovalPage />} />
        
        {/* 2. 발행 관리 페이지 (최종 운영 배포) */}
        <Route path="publish" element={<HqPublishPage />} />
      </Route>

      {/* 예외 경로 처리 */}
      <Route path="/" element={<Navigate to="/hq/approvals" replace />} />
    </Routes>
  );
}

export default App;