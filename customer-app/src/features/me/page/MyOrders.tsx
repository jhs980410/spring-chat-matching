// me/page/MyOrders.tsx
import { useEffect, useState } from "react";
import {
  Table,
  Title,
  Text,
  Button,
  Divider,
  Box,
  Badge,
} from "@mantine/core";
import { useNavigate } from "react-router-dom";
import api from "../../../api/axios";

type Order = {
  orderId: number;
  orderStatus: "PAID" | "CANCELLED" | "REFUNDED" | "PENDING";
  orderedAt: string | null;
  totalPrice: number;
  event: {
    id: number;
    title: string;
    thumbnail: string;
    startAt: string;
  };
  items: {
    ticketName: string;
    quantity: number;
    unitPrice: number;
  }[];
};

const STATUS_MAP: Record<
  Order["orderStatus"],
  { label: string; color: string }
> = {
   ORDERED: { label: "주문 완료", color: "gray" },
  PAID: { label: "예매 완료", color: "blue" },
  CANCELLED: { label: "취소", color: "red" },
  REFUNDED: { label: "환불", color: "orange" },
  PENDING: { label: "결제 대기", color: "gray" },
};

export default function MyOrders() {
  const [orders, setOrders] = useState<Order[]>([]);
  const navigate = useNavigate();

  useEffect(() => {
    api.get("/me/orders").then((res) => {
      setOrders(res.data);
    });
  }, []);

  return (
    <Box>
      <Title order={3} mb="md">
        예매내역
      </Title>

      <Divider mb="md" />

      <Table striped withTableBorder verticalSpacing="md">
        <Table.Thead>
          <Table.Tr>
            <Table.Th>공연명</Table.Th>
            <Table.Th>관람일</Table.Th>
            <Table.Th>매수</Table.Th>
            <Table.Th>총 결제금액</Table.Th>
            <Table.Th>상태</Table.Th>
            <Table.Th />
          </Table.Tr>
        </Table.Thead>

        <Table.Tbody>
          {orders.map((order) => {
            const totalQuantity = order.items.reduce(
              (sum, i) => sum + i.quantity,
              0
            );
            const status = STATUS_MAP[order.orderStatus];

            return (
              <Table.Tr
                key={order.orderId}
                onClick={() => navigate(`/me/orders/${order.orderId}`)}
                style={{
                  cursor: "pointer",
                  transition: "background-color 0.15s ease",
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.backgroundColor = "#f8f9fa";
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.backgroundColor = "";
                }}
              >
                {/* 공연명 */}
                <Table.Td>
                  <Text fw={600}>{order.event.title}</Text>
                </Table.Td>

                {/* 관람일 */}
                <Table.Td>
                  {new Date(order.event.startAt).toLocaleDateString()}
                </Table.Td>

                {/* 매수 */}
                <Table.Td>{totalQuantity}매</Table.Td>

                {/* 총 금액 */}
                <Table.Td>
                  {order.totalPrice.toLocaleString()}원
                </Table.Td>

                {/* 상태 */}
                <Table.Td>
                  <Badge color={status.color}>{status.label}</Badge>
                </Table.Td>

                {/* 상세 버튼 */}
                <Table.Td onClick={(e) => e.stopPropagation()}>
                  <Button
                    size="xs"
                    variant="subtle"
                    onClick={() =>
                      navigate(`/me/orders/${order.orderId}`)
                    }
                  >
                    상세
                  </Button>
                </Table.Td>
              </Table.Tr>
            );
          })}
        </Table.Tbody>
      </Table>

      {orders.length === 0 && (
        <Text mt="lg" c="dimmed">
          예매 내역이 없습니다.
        </Text>
      )}
    </Box>
  );
}
