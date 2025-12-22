// me/sidebar/MySidebar.tsx
import { NavLink } from "react-router-dom";
import { Box, Text, Divider } from "@mantine/core";

const baseLinkStyle = {
  padding: "10px 14px",
  borderRadius: "8px",
  textDecoration: "none",
  fontSize: "14px",
  transition: "all 0.15s ease",
  display: "block",
};

export default function MySidebar() {
  return (
    <Box
      w={220}
      p="md"
      style={{
        background: "#ffffff",
        borderRadius: "12px",
        boxShadow: "0 4px 16px rgba(0,0,0,0.06)",
      }}
    >
      <Text fw={700} mb="md" size="sm" c="dimmed">
        마이페이지
      </Text>

      {/* 예매 */}
      <Section title="예매">
        <SidebarLink to="/me" end label="홈" />
        <SidebarLink to="/me/orders" label="예매내역" />
      </Section>

      <Divider my="sm" />

      {/* 예매자 정보 */}
      <Section title="예매자 정보">
        <SidebarLink to="/me/reserve-users" label="예매자 관리" disabled />
      </Section>

      <Divider my="sm" />

      {/* 배송 / 결제 */}
      <Section title="배송 / 결제">
        <SidebarLink to="/me/deliveries" label="배송 정보" disabled />
        <SidebarLink to="/me/payments" label="결제수단 관리" disabled />
      </Section>

      <Divider my="sm" />

      {/* 혜택 */}
      <Section title="혜택">
        <SidebarLink to="/me/coupons" label="쿠폰함" disabled />
        <SidebarLink to="/me/points" label="포인트" disabled />
      </Section>

      <Divider my="sm" />

      {/* 계정 */}
      <Section title="계정">
        <SidebarLink to="/me/profile" label="회원정보 수정" />
      </Section>
    </Box>
  );
}

/* ----------------- */
/* Sub Components    */
/* ----------------- */

function Section({
  title,
  children,
}: {
  title: string;
  children: React.ReactNode;
}) {
  return (
    <Box>
      <Text fw={600} mb={6} size="xs" c="dimmed">
        {title}
      </Text>
      <Box style={{ display: "flex", flexDirection: "column", gap: 6 }}>
        {children}
      </Box>
    </Box>
  );
}

function SidebarLink({
  to,
  label,
  end,
  disabled,
}: {
  to: string;
  label: string;
  end?: boolean;
  disabled?: boolean;
}) {
  if (disabled) {
    return (
      <Box
        style={{
          ...baseLinkStyle,
          color: "#adb5bd",
          cursor: "not-allowed",
        }}
      >
        {label}
        <Text size="xs" c="dimmed">
          준비 중
        </Text>
      </Box>
    );
  }

  return (
    <NavLink
      to={to}
      end={end}
      style={({ isActive }) => ({
        ...baseLinkStyle,
        backgroundColor: isActive ? "#e7f5ff" : "transparent",
        color: isActive ? "#1c7ed6" : "#495057",
        fontWeight: isActive ? 600 : 400,
      })}
    >
      {label}
    </NavLink>
  );
}
