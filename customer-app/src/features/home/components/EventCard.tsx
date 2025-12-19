// features/home/components/EventCard.tsx
import {
  Card,
  Image,
  Text,
  Badge,
  Group,
  Stack,
} from "@mantine/core";
import type { EventSummary } from "../mock/home.mock";
import { useState } from "react";
import { useNavigate } from "react-router-dom"; // 

interface Props {
  event: EventSummary;
}

export default function EventCard({ event }: Props) {
  const [hovered, setHovered] = useState(false);
  const navigate = useNavigate(); // 

  return (
    <Card
      radius="md"
      withBorder
      p="md"
      style={{
        cursor: "pointer",
        transition: "all 0.2s ease",
        transform: hovered ? "translateY(-4px)" : "none",
        boxShadow: hovered
          ? "0 8px 20px rgba(0,0,0,0.12)"
          : "none",
      }}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
      onClick={() => navigate(`/events/${event.id}`)} // ✅ 핵심
    >
      <Card.Section style={{ overflow: "hidden" }}>
        <Image
          src={event.thumbnail}
          height={160}
          alt={event.title}
          fallbackSrc="https://placehold.co/300x160?text=EVENT"
          style={{
            transition: "transform 0.3s ease",
            transform: hovered ? "scale(1.05)" : "scale(1)",
          }}
        />
      </Card.Section>

      <Stack mt="sm" gap={6}>
        <Group justify="space-between">
          {event.badge && (
            <Badge
              color={
                event.badge === "HOT"
                  ? "red"
                  : event.badge === "NEW"
                  ? "blue"
                  : "orange"
              }
              variant="light"
            >
              {event.badge}
            </Badge>
          )}

          {event.ranking && (
            <Badge variant="outline">#{event.ranking}</Badge>
          )}
        </Group>

        <Text fw={600} lineClamp={2} style={{ lineHeight: 1.4 }}>
          {event.title}
        </Text>

        {event.openDate && (
          <Text size="sm" c="dimmed">
            {event.openDate} 오픈
          </Text>
        )}
      </Stack>
    </Card>
  );
}
