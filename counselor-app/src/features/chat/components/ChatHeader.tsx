import { Group, Text, Badge, Divider } from "@mantine/core";

interface SessionInfo {
  categoryName: string;
  status: string;
}

export default function ChatHeader({ session }: { session: SessionInfo }) {
  return (
    <>
      <Group justify="space-between" mb="sm">
        <Text fw={700}>{session.categoryName}</Text>

        <Badge color={session.status === "IN_PROGRESS" ? "blue" : "gray"}>
          {session.status}
        </Badge>
      </Group>

      <Divider mb="md" />
    </>
  );
}
