import styles from "./ReserveSummaryPanel.module.css";
import { getTossPayments } from "../../../payment/toss";
import { useParams } from "react-router-dom";
import { useState } from "react";
import api from "../../../../api/axios";

interface Props {
  selectedSeatIds: number[];
  price: number;
  seatLabelMap: Record<
    number,
    {
      rowLabel: string;
      seatNumber: number;
    }
  >;
}

export default function ReserveSummaryPanel({
  selectedSeatIds,
  price,
  seatLabelMap,
}: Props) {
  const total = selectedSeatIds.length * price;
  
  const { id } = useParams<{ id: string }>();
  const eventId = Number(id);

  const [loading, setLoading] = useState(false); // 🔥 중복 클릭 방지

  const handlePay = async () => {
    if (loading) return;

    if (selectedSeatIds.length === 0) {
      alert("좌석을 선택해주세요.");
      return;
    }

    try {
      setLoading(true);

      // 1️⃣ 주문 생성 (PENDING)
      const orderRes = await api.post("/orders", {
        eventId,
        seatIds: selectedSeatIds,
      });

      const { orderId } = orderRes.data;

      // 2️⃣ 좌석 락
      await api.post(`/orders/${orderId}/reserve`, {
        eventId,
        seatIds: selectedSeatIds,
      });

      // 3️⃣ Toss 결제
      const tossPayments = await getTossPayments();
      const payment = tossPayments.payment({
        customerKey: `user_${orderId}`,
      });

      await payment.requestPayment({
        method: "CARD",
        amount: {
          currency: "KRW",
          value: total,
        },
        orderId: `ORD-${orderId.toString().padStart(6, "0")}`,
        orderName: `좌석 ${selectedSeatIds.length}매`,
        customerName: "테스트고객",
        successUrl: `${window.location.origin}/payment/confirm`,
        failUrl: `${window.location.origin}/payment/fail`,
      });
    } catch (e: any) {
      console.error(e);

      // 🔥 reserve 실패 / 네트워크 오류
      alert(
        e?.response?.data?.message ??
          "예매 처리 중 오류가 발생했습니다. 다시 시도해주세요."
      );
      setLoading(false);
    }
  };

  return (
    <div className={styles.panel}>
      <h3 className={styles.title}>선택 좌석</h3>

      {selectedSeatIds.length === 0 ? (
        <p className={styles.empty}>선택한 좌석이 없습니다.</p>
      ) : (
        <ul className={styles.seatList}>
          {selectedSeatIds.map((seatId) => {
            const seat = seatLabelMap[seatId];
            if (!seat) return null;

            return (
              <li key={seatId} className={styles.seatItem}>
                {seat.rowLabel}열 {seat.seatNumber}번
              </li>
            );
          })}
        </ul>
      )}

      <div className={styles.divider} />

      <div className={styles.total}>
        총 금액 <strong>{total.toLocaleString()}원</strong>
      </div>

      <button
        className={styles.reserveButton}
        disabled={selectedSeatIds.length === 0 || loading}
        onClick={handlePay}
      >
        {loading ? "처리 중..." : "예매하기"}
      </button>
    </div>
  );
}
