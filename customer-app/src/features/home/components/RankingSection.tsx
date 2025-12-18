// features/home/components/RankingSection.tsx
import { Stack, Title, SimpleGrid, Text } from "@mantine/core";
import EventCard from "./EventCard";
import { homeMock } from "../mock/home.mock";
import type { Category } from "../mock/home.mock";

interface Props {
  category: Category;
}

export default function RankingSection({ category }: Props) {
  const events = homeMock.rankings[category];

  return (
    <Stack gap="md" mt="xl">

      <Title order={3}>장르별 랭킹</Title>

      {events.length === 0 ? (
        <Text c="dimmed">해당 장르의 랭킹 정보가 없습니다.</Text>
      ) : (
        <SimpleGrid cols={3} spacing="md">
          {events.map((event) => (
            <EventCard
              key={event.id}
              event={{
                ...event,
                category,
              }}
            />
          ))}
        </SimpleGrid>
      )}
    </Stack>
  );
}
