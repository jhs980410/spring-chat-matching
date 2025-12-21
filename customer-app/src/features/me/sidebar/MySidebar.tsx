// me/sidebar/MySidebar.tsx
import { NavLink } from "react-router-dom";

export default function MySidebar() {
      console.log("MySidebar rendered");
  return (
    <aside
      style={{
        width: "240px",
        borderRight: "1px solid #eee",
        padding: "24px",
      }}
    >
      <h3 style={{ marginBottom: "16px" }}>마이페이지</h3>

      <nav style={{ display: "flex", flexDirection: "column", gap: "12px" }}>
        <NavLink to="/me" end>홈</NavLink>
        <NavLink to="/me/orders">예매내역</NavLink>
        <NavLink to="/me/profile">회원정보 수정</NavLink>
      </nav>
    </aside>
  );
}
