import type { Seat } from "./types";
import styles from "./ReserveSummaryPanel.module.css";
import { getTossPayments } from "../../../payment/toss";

interface Props {
  selectedSeats: Seat[];
  price: number;
}

export default function ReserveSummaryPanel({
  selectedSeats,
  price,
}: Props) {
  const total = selectedSeats.length * price;

  const handlePay = async () => {
    if (selectedSeats.length === 0) {
      alert("좌석을 선택해주세요.");
      return;
    }

    // ⚠️ 실제로는 여기서
    // 1) payment-ready API 호출
    // 2) orderId 받아와야 함
    const orderId = `ORDER_${Date.now()}`;

    const tossPayments = await getTossPayments();

    await tossPayments.requestPayment("CARD", {
      amount: total,
      orderId,
      orderName: `좌석 ${selectedSeats.length}매`,
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
