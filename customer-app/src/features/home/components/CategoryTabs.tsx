// features/home/components/CategoryTabs.tsx
import { Group, Button } from "@mantine/core";
import type { Category } from "../mock/home.mock";

const categories: { label: string; value: Category }[] = [
  { label: "뮤지컬", value: "MUSICAL" },
  { label: "콘서트", value: "CONCERT" },
  { label: "스포츠", value: "SPORTS" },
  { label: "전시/행사", value: "EXHIBITION" },
  { label: "연극", value: "THEATER" },
];

interface Props {
  value: Category;
  onChange: (category: Category) => void;
}

export default function CategoryTabs({ value, onChange }: Props) {
  return (
    <Group>
      {categories.map((c) => (
        <Button
          key={c.value}
          variant={value === c.value ? "filled" : "light"}
          onClick={() => onChange(c.value)}
        >
          {c.label}
        </Button>
      ))}
    </Group>
  );
}
