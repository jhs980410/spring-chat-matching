import { useEffect, useState } from "react";
import {
  Box,
  Card,
  Text,
  Group,
  Button,
  Badge,
  Stack,
  Center,
  Loader,
} from "@mantine/core";
import { useNavigate } from "react-router-dom";
import api from "../../../api/axios";

type ReserveUser = {
  id: number;
  realName: string;
  phone: string;
  email: string;
  birth: string;
  default: boolean;
};

export default function MyReserveUsers() {
  const [users, setUsers] = useState<ReserveUser[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const res = await api.get<ReserveUser[]>("/me/reserve-users");
      setUsers(res.data);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm("이 예매자를 삭제하시겠습니까?")) return;
    await api.delete(`/me/reserve-users/${id}`);
    fetchUsers();
  };

  if (loading) {
    return (
      <Center h={200}>
        <Loader />
      </Center>
    );
  }

  return (
    <Box>
      {/* Header */}
      <Group justify="space-between" mb="md">
        <Text fw={700} size="lg">
          예매자 관리
        </Text>

        <Button onClick={() => navigate("/me/reserve-users/new")}>
          예매자 추가
        </Button>
      </Group>

      {/* List */}
      <Stack gap="sm">
        {users.map((user) => (
          <Card key={user.id} withBorder radius="md">
            <Group justify="space-between" align="flex-start">
              <Box>
                <Group gap={8}>
                  <Text fw={600}>{user.realName}</Text>
                  {user.default && (
                    <Badge color="blue" variant="light">
                      기본 예매자
                    </Badge>
                  )}
                </Group>

                <Text size="sm" c="dimmed">
                  {user.phone}
                </Text>
                <Text size="sm" c="dimmed">
                  {user.email}
                </Text>
                <Text size="xs" c="dimmed">
                  생년월일 · {user.birth}
                </Text>
              </Box>

              <Group gap={6}>
                <Button
                  size="xs"
                  variant="light"
                  onClick={() =>
                    navigate(`/me/reserve-users/${user.id}`)
                  }
                >
                  수정
                </Button>
                <Button
                  size="xs"
                  color="red"
                  variant="light"
                  onClick={() => handleDelete(user.id)}
                >
                  삭제
                </Button>
              </Group>
            </Group>
          </Card>
        ))}

        {users.length === 0 && (
          <Card withBorder>
            <Text c="dimmed" size="sm">
              등록된 예매자가 없습니다.
            </Text>
          </Card>
        )}
      </Stack>
    </Box>
  );
}
