// layouts/TicketSubNav.tsx
import { Group, Button } from "@mantine/core";
import { useNavigate } from "react-router-dom";

const categories = [
  { label: "뮤지컬", path: "/category/musical" },
  { label: "콘서트", path: "/category/concert" },
  { label: "스포츠", path: "/category/sports" },
  { label: "전시/행사", path: "/category/exhibition" },
  { label: "연극", path: "/category/theater" },
];

export default function TicketSubNav() {
  const navigate = useNavigate();

  return (
    <Group
      px="lg"
      py="sm"
      gap="xs"
      style={{
        borderBottom: "1px solid #eee",
        background: "white",
        position: "sticky",
        top: 56, // Header 높이 기준
        zIndex: 9,
      }}
    >
      {categories.map((c) => (
        <Button
          key={c.label}
          variant="subtle"
          size="sm"
          onClick={() => navigate(c.path)}
        >
          {c.label}
        </Button>
      ))}

      <Button variant="subtle" size="sm" c="blue">
        랭킹
      </Button>
      <Button variant="subtle" size="sm" c="blue">
        오픈예정
      </Button>
    </Group>
  );
}
