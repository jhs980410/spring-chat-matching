// me/sidebar/MySidebar.tsx
import { NavLink } from "react-router-dom";
import { Box, Text } from "@mantine/core";

const linkStyle = {
  padding: "10px 12px",
  borderRadius: "6px",
  textDecoration: "none",
  color: "#333",
};

export default function MySidebar() {
  return (
    <Box
      w={220}
      p="md"
      style={{ borderRight: "1px solid #e5e7eb" }}
    >
      <Text fw={700} mb="md">
        마이페이지
      </Text>

      <Box style={{ display: "flex", flexDirection: "column", gap: 4 }}>
        <NavLink
          to="/me"
          end
          style={({ isActive }) => ({
            ...linkStyle,
            backgroundColor: isActive ? "#e7f5ff" : "transparent",
            color: isActive ? "#1c7ed6" : "#333",
            fontWeight: isActive ? 600 : 400,
          })}
        >
          홈
        </NavLink>

        <NavLink
          to="/me/orders"
          style={({ isActive }) => ({
            ...linkStyle,
            backgroundColor: isActive ? "#e7f5ff" : "transparent",
            color: isActive ? "#1c7ed6" : "#333",
            fontWeight: isActive ? 600 : 400,
          })}
        >
          예매내역
        </NavLink>

        <NavLink
          to="/me/profile"
          style={({ isActive }) => ({
            ...linkStyle,
            backgroundColor: isActive ? "#e7f5ff" : "transparent",
            color: isActive ? "#1c7ed6" : "#333",
            fontWeight: isActive ? 600 : 400,
          })}
        >
          회원정보 수정
        </NavLink>
      </Box>
    </Box>
  );
}
