// me/page/MyOrders.tsx
import { useEffect, useState } from "react";
import {
  Table,
  Title,
  Group,
  Text,
  Button,
  Divider,
  Box,
} from "@mantine/core";
import { useNavigate } from "react-router-dom";
import api from "../../../api/axios";

type Order = {
  orderId: number;
  orderStatus: string;
  orderedAt: string | null;
  totalPrice: number;
  event: {
    eventId: number;
    title: string;
    thumbnail: string;
    venue: string;
    startAt: string;
  };
  items: {
    ticketName: string;
    quantity: number;
    unitPrice: number;
  }[];
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
        최근 예매내역
      </Title>

      <Divider mb="md" />

      <Table
        striped
        highlightOnHover
        withTableBorder
        verticalSpacing="md"
      >
        <Table.Thead>
          <Table.Tr>
            <Table.Th>공연명</Table.Th>
            <Table.Th>관람일</Table.Th>
            <Table.Th>매수</Table.Th>
            <Table.Th>총 결제금액</Table.Th>
            <Table.Th>상태</Table.Th>
            <Table.Th></Table.Th>
          </Table.Tr>
        </Table.Thead>

        <Table.Tbody>
          {orders.map((order) => {
            const totalQuantity = order.items.reduce(
              (sum, i) => sum + i.quantity,
              0
            );

            return (
              <Table.Tr key={order.orderId}>
                {/* 공연명 */}
                <Table.Td>
                  <Text fw={600}>{order.event.title}</Text>
                  <Text size="sm" c="dimmed">
                    {order.event.venue}
                  </Text>
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
                  <Text c={order.orderStatus === "PAID" ? "blue" : "red"}>
                    {order.orderStatus}
                  </Text>
                </Table.Td>

                {/* 상세 */}
                <Table.Td>
                  <Button
                    size="xs"
                    variant="outline"
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
