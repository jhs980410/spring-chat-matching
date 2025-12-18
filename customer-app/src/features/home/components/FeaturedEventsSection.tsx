// features/home/components/FeaturedEventsSection.tsx
import { SimpleGrid, Title, Stack, Text } from "@mantine/core";
import EventCard from "./EventCard";
import { homeMock } from "../mock/home.mock";
import type { Category } from "../mock/home.mock";

interface Props {
  category: Category;
}

export default function FeaturedEventsSection({ category }: Props) {
  const events = homeMock.featuredEvents.filter(
    (event) => event.category === category
  );

  return (
    <Stack gap="md" mt="xl">

      <Title order={3}>추천 이벤트</Title>

      {events.length === 0 ? (
        <Text c="dimmed">해당 카테고리의 추천 이벤트가 없습니다.</Text>
      ) : (
        <SimpleGrid cols={3} spacing="md">
          {events.map((event) => (
            <EventCard key={event.id} event={event} />
          ))}
        </SimpleGrid>
      )}
    </Stack>
  );
}
