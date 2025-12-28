import type { Seat } from "./types";
import styles from "./ReserveSummaryPanel.module.css";
import { getTossPayments } from "../../../payment/toss";
import { useParams } from "react-router-dom";
import api from "../../../../api/axios";

interface Props {
  selectedSeats: Seat[];
  price: number;
}

export default function ReserveSummaryPanel({
  selectedSeats,
  price

}: Props) {
  const total = selectedSeats.length * price;

const { id } = useParams<{ id: string }>();
const eventId = Number(id);
const handlePay = async () => {
  if (selectedSeats.length === 0) {
    alert("좌석을 선택해주세요.");
    return;
  }

  // 1️⃣ 빈 주문 생성 (PENDING)
  const orderRes = await api.post("/orders", {
  eventId
});
  const { orderId } = orderRes.data;
const tossOrderId = `order_${orderId}_${Date.now()}`;
  // 2️⃣ 좌석 락 (orderId 기준)
await api.post(`/orders/${orderId}/reserve`, {
  eventId,
  seatIds: selectedSeats.map((s) => s.id),
});

  // 3️⃣ Toss 결제창
  const tossPayments = await getTossPayments();
  const payment = tossPayments.payment({
    customerKey: "user_" + orderId,
  });

  await payment.requestPayment({
    method: "CARD",
    amount: {
      currency: "KRW",
      value: total,
    },
    orderId: tossOrderId,
    orderName: `좌석 ${selectedSeats.length}매`,
    customerName: "테스트고객",
    successUrl: `${window.location.origin}/payment/success`,
    failUrl: `${window.location.origin}/payment/fail`,
  });
};

  return (
    <div className={styles.panel}>
      <h3 className={styles.title}>선택 좌석</h3>

      {selectedSeats.length === 0 ? (
        <p className={styles.empty}>선택한 좌석이 없습니다.</p>
      ) : (
        <ul className={styles.seatList}>
          {selectedSeats.map((s) => (
            <li key={s.id} className={styles.seatItem}>
              {s.row}열 {s.number}번
            </li>
          ))}
        </ul>
      )}

      <div className={styles.divider} />

      <div className={styles.total}>
        총 금액 <strong>{total.toLocaleString()}원</strong>
      </div>

      <button
        className={styles.reserveButton}
        disabled={selectedSeats.length === 0}
        onClick={handlePay}
      >
        예매하기
      </button>
    </div>
  );
}
