import { Container, Grid } from "@mantine/core";
import { useState } from "react";

import ReserveTicketList from "../components/reserve/ReserveTicketList";
import ReserveSummaryPanel from "../components/reserve/ReserveSummaryPanel";
import type { TicketOption } from "../types/reserveTypes";

const MOCK_TICKETS: TicketOption[] = [
  { id: 1, name: "R석", price: 120000, remain: 50 },
  { id: 2, name: "S석", price: 90000, remain: 120 },
];

export default function ReservePage() {
  const [selected, setSelected] = useState<TicketOption | null>(null);

  return (
    <Container size="lg">
      <Grid gutter="xl">
        <Grid.Col span={8}>
          <ReserveTicketList
            tickets={MOCK_TICKETS}
            selected={selected}
            onSelect={setSelected}
          />
        </Grid.Col>

        <Grid.Col span={4}>
          <ReserveSummaryPanel selected={selected} />
        </Grid.Col>
      </Grid>
    </Container>
  );
}
