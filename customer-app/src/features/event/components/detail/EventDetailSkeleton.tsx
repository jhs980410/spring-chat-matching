// features/event/components/EventDetailSkeleton.tsx
import { Container, Grid, Skeleton, Stack } from "@mantine/core";

export default function EventDetailSkeleton() {
  return (
    <Container size="lg">
      <Grid gutter="xl">
        <Grid.Col span={8}>
          <Stack gap="lg">
            <Skeleton height={360} radius="md" />
            <Skeleton height={24} width="60%" />
            <Skeleton height={16} />
            <Skeleton height={16} />
            <Skeleton height={120} />
          </Stack>
        </Grid.Col>

        <Grid.Col span={4}>
          <Skeleton height={260} radius="md" />
        </Grid.Col>
      </Grid>
    </Container>
  );
}
