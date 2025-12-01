import { Group, Text, Badge, Divider } from "@mantine/core";

export default function ChatHeader({ session }: any) {
  return (
    <>
      <Group position="apart" mb="sm">
        <Text fw={700}>{session.category_name}</Text>

        <Badge color={session.status === "IN_PROGRESS" ? "blue" : "gray"}>
          {session.status}
        </Badge>
      </Group>

      <Divider mb="md" />
    </>
  );
}
