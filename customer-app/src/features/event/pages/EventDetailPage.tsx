import { Container, Grid, Stack } from "@mantine/core";
import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import api from "../../../api/axios";

import EventHero from "../components/detail/EventHero";
import EventInfoPanel from "../components/detail/EventInfoPanel";
import EventDescription from "../components/detail/EventDescription";
import TicketOptionList from "../components/detail/TicketOptionList";
import EventDetailSkeleton from "../components/detail/EventDetailSkeleton";

import type { EventDetail } from "../types/eventTypes";

export default function EventDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [event, setEvent] = useState<EventDetail | null>(null);

  useEffect(() => {
    api.get(`/events/${id}`).then((res) => setEvent(res.data));
  }, [id]);

  if (!event) return <EventDetailSkeleton />;

  return (
    <Container size="lg">
      <Grid gutter="xl">
        <Grid.Col span={8}>
          <Stack gap="xl">
            <EventHero event={event} />
            <EventDescription event={event} />
            <TicketOptionList tickets={event.ticketOptions} />
          </Stack>
        </Grid.Col>

        <Grid.Col span={4}>
          <EventInfoPanel event={event} />
        </Grid.Col>
      </Grid>
    </Container>
  );
}
