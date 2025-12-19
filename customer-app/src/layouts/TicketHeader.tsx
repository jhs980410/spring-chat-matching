// layouts/components/TicketHeader.tsx
import { Group, Text, Button, Container } from "@mantine/core";
import { useNavigate } from "react-router-dom";

export default function TicketHeader() {
  const navigate = useNavigate();

  return (
    <Container size="xl" py="md">
      <Group justify="space-between">
        <Text
          fw={800}
          size="xl"
          style={{ cursor: "pointer" }}
          onClick={() => navigate("/")}
        >
          TICKET
        </Text>

        <Group>
          <Button variant="subtle" onClick={() => navigate("/login")}>
            로그인
          </Button>
        </Group>
      </Group>
    </Container>
  );
}
