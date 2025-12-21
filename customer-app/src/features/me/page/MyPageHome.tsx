// me/layout/MyPageLayout.tsx
import { Outlet } from "react-router-dom";

export default function MyPageLayout() {
  return (
    <div style={{ display: "flex", minHeight: "100vh" }}>

      {/* 우측 컨텐츠 */}
      <main style={{ flex: 1, padding: "24px" }}>
        <Outlet />
      </main>
    </div>
  );
}
