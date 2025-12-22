// me/page/MyOrderDetail.tsx
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
  Box,
  Title,
  Text,
  Group,
  Divider,
  Card,
  Badge,
  Image,
} from "@mantine/core";
import api from "../../../api/axios";

const STATUS_MAP: Record<
  "PAID" | "CANCELLED" | "REFUNDED" | "PENDING",
  { label: string; color: string }
> = {
  PAID: { label: "예매 완료", color: "blue" },
  CANCELLED: { label: "취소", color: "red" },
  REFUNDED: { label: "환불", color: "orange" },
  PENDING: { label: "결제 대기", color: "gray" },
};

export default function MyOrderDetail() {
  const { orderId } = useParams();
  const [order, setOrder] = useState<any>(null);

  useEffect(() => {
    api.get(`/me/orders/${orderId}`).then((res) => {
      setOrder(res.data);
    });
  }, [orderId]);

  if (!order) return null;

  const status = STATUS_MAP[order.orderStatus];

  return (
    <Box>
      <Title order={3} mb="lg">
        예매 상세
      </Title>

      {/* 1️⃣ 주문 요약 */}
      <Card withBorder mb="md">
        <Group justify="space-between">
          <Box>
            <Text fw={700}>주문번호 {order.orderId}</Text>
            <Text size="sm" c="dimmed">
              예매일 {new Date(order.orderedAt).toLocaleString()}
            </Text>
          </Box>
          <Badge color={status.color}>{status.label}</Badge>
        </Group>
      </Card>

      {/* 2️⃣ 공연 정보 */}
      <Card withBorder mb="md">
        <Group>
          <Image
            src={order.event.thumbnail}
            w={100}
            radius="sm"
          />
          <Box>
            <Text fw={700}>{order.event.title}</Text>
            <Text size="sm">
              {new Date(order.event.startAt).toLocaleString()}
            </Text>
          </Box>
        </Group>
      </Card>

      {/* 3️⃣ 좌석 / 권종 */}
      <Card withBorder mb="md">
        <Title order={5} mb="sm">
          예매 내역
        </Title>

        {order.items.map((item: any, idx: number) => (
          <Group key={idx} justify="space-between">
            <Text>
              {item.ticketName} × {item.quantity}
            </Text>
            <Text fw={600}>
              {(item.unitPrice * item.quantity).toLocaleString()}원
            </Text>
          </Group>
        ))}

        <Divider my="sm" />

        <Group justify="space-between">
          <Text fw={700}>총 결제금액</Text>
          <Text fw={700}>
            {order.totalPrice.toLocaleString()}원
          </Text>
        </Group>
      </Card>

      {/* 4️⃣ 결제 정보 */}
      <Card withBorder>
        <Title order={5} mb="sm">
          결제 정보
        </Title>
        <Text size="sm">결제 수단: 카드</Text>
        <Text size="sm">
          결제 일시:{" "}
          {order.paidAt
            ? new Date(order.paidAt).toLocaleString()
            : "-"}
        </Text>
      </Card>
    </Box>
  );
}
