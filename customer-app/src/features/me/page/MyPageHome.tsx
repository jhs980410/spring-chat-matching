import { useEffect, useState } from "react";
import {
  Box,
  Card,
  Title,
  Text,
  Group,
  SimpleGrid,
  Badge,
  Divider,
  Button,
  Image,
} from "@mantine/core";
import { useNavigate } from "react-router-dom";
import api from "../../../api/axios";

const STATUS_MAP: Record<string, { label: string; color: string }> = {
  PAID: { label: "예매 완료", color: "blue" },
  CANCELLED: { label: "취소", color: "red" },
  REFUNDED: { label: "환불", color: "orange" },
  PENDING: { label: "결제 대기", color: "gray" },
};

export default function MyPageHome() {
  const [data, setData] = useState<any>(null);
  const navigate = useNavigate();

  useEffect(() => {
    api.get("/me/home").then((res) => setData(res.data));
  }, []);

  if (!data) return null;

  const { user, orderSummary, recentOrders } = data;

  return (
    <Card
  withBorder
  radius="md"
  p="xl"
  style={{
    boxShadow: "0 8px 24px rgba(0,0,0,0.06)",
  }}
>
      <Title order={3} mb="lg">
        마이페이지
      </Title>

      {/* 1️⃣ 회원 요약 */}
      <Card withBorder mb="xl" radius="md">
        <Group justify="space-between">
          <Box>
            <Text fw={700} size="lg">
              {user.nickname} 님
            </Text>
            <Badge mt={6} variant="light">
              {user.grade}
            </Badge>
          </Box>

          <Group gap="xl">
            <Box ta="right">
              <Text size="sm" c="dimmed">
                쿠폰
              </Text>
              <Text fw={700}>{user.couponCount}장</Text>
            </Box>
            <Box ta="right">
              <Text size="sm" c="dimmed">
                포인트
              </Text>
              <Text fw={700}>{user.point.toLocaleString()}P</Text>
            </Box>
          </Group>
        </Group>
      </Card>

      {/* 2️⃣ 예매 요약 */}
      <SimpleGrid cols={5} mb="xl">
        <Summary label="전체 예매" value={orderSummary.total} />
        <Summary label="예매 완료" value={orderSummary.paid} color="blue" />
        <Summary label="취소" value={orderSummary.cancelled} color="red" />
        <Summary label="환불" value={orderSummary.refunded} color="orange" />
        <Summary label="관람 완료" value={orderSummary.completed} />
      </SimpleGrid>

      {/* 3️⃣ 최근 예매내역 */}
      <Group justify="space-between" mb="sm">
        <Title order={4}>최근 예매내역</Title>
        <Button variant="subtle" onClick={() => navigate("/me/orders")}>
          전체보기
        </Button>
      </Group>

      <Divider mb="md" />

      {recentOrders.length === 0 && (
        <Text c="dimmed" ta="center" py="xl">
          최근 예매 내역이 없습니다.
        </Text>
      )}

      {recentOrders.map((order: any) => {
        const status = STATUS_MAP[order.status];

        return (
          <Card
            key={order.orderId}
            withBorder
            mb="sm"
            radius="md"
            style={{
              cursor: "pointer",
              transition: "all 0.15s ease",
            }}
            onClick={() => navigate(`/me/orders/${order.orderId}`)}
            onMouseEnter={(e) =>
              (e.currentTarget.style.boxShadow =
                "0 6px 16px rgba(0,0,0,0.08)")
            }
            onMouseLeave={(e) =>
              (e.currentTarget.style.boxShadow =
                "0 2px 8px rgba(0,0,0,0.04)")
            }
          >
            <Group align="center">
              <Image src={order.event.thumbnail} w={80} radius="sm" />

              <Box flex={1}>
                <Text fw={600}>{order.event.title}</Text>

                <Text size="sm" c="dimmed">
                  {order.event.categoryName ?? "공연"} ·{" "}
                  {new Date(order.event.startAt).toLocaleDateString()}
                </Text>

                <Group gap={6} mt={4}>
                  <Badge size="sm" variant="light">
                    {order.quantity}매
                  </Badge>

                  {order.event.badge && (
                    <Badge size="sm">{order.event.badge}</Badge>
                  )}
                </Group>
              </Box>

              <Box ta="right">
                <Badge color={status.color}>{status.label}</Badge>
                <Text fw={600} mt={4}>
                  {order.totalPrice.toLocaleString()}원
                </Text>
              </Box>
            </Group>
          </Card>
        );
      })}
    </Card>
  );
}

function Summary({
  label,
  value,
  color,
}: {
  label: string;
  value: number;
  color?: string;
}) {
  return (
    <Card withBorder ta="center" radius="md">
      <Text size="sm" c="dimmed">
        {label}
      </Text>
      <Text fw={700} size="xl" c={color}>
        {value}
      </Text>
    </Card>
  );
}
