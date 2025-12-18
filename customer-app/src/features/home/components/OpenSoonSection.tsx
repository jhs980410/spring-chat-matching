// features/home/components/OpenSoonSection.tsx
import { Stack, Title, SimpleGrid, Text } from "@mantine/core";
import EventCard from "./EventCard";
import { homeMock } from "../mock/home.mock";
import type { Category } from "../mock/home.mock";

interface Props {
  category: Category;
}

export default function OpenSoonSection({ category }: Props) {
  const events = homeMock.openSoonEvents.filter(
    (event) => event.category === category
  );

  return (
    <Stack gap="md" mt="xl">

      <Title order={3}>오픈 예정</Title>

      {events.length === 0 ? (
        <Text c="dimmed">해당 카테고리의 오픈 예정 이벤트가 없습니다.</Text>
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
