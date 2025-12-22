// me/sidebar/MySidebar.tsx
import { NavLink } from "react-router-dom";
import { Box, Text } from "@mantine/core";

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

      <Box style={{ display: "flex", flexDirection: "column", gap: 6 }}>
        <SidebarLink to="/me" end label="홈" />
        <SidebarLink to="/me/orders" label="예매내역" />
        <SidebarLink to="/me/profile" label="회원정보 수정" />
      </Box>
    </Box>
  );
}

function SidebarLink({
  to,
  label,
  end,
}: {
  to: string;
  label: string;
  end?: boolean;
}) {
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
      onMouseEnter={(e) => {
        if (!(e.currentTarget as HTMLElement).dataset.active) {
          e.currentTarget.style.backgroundColor = "#f8f9fa";
        }
      }}
      onMouseLeave={(e) => {
        if (!(e.currentTarget as HTMLElement).dataset.active) {
          e.currentTarget.style.backgroundColor = "transparent";
        }
      }}
    >
      {label}
    </NavLink>
  );
}
