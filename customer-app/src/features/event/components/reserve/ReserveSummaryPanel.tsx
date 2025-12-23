import type { Seat } from "./types";
import styles from "./ReserveSummaryPanel.module.css";

interface Props {
  selectedSeats: Seat[];
  price: number;
}

export default function ReserveSummaryPanel({
  selectedSeats,
  price,
}: Props) {
  const total = selectedSeats.length * price;

  return (
    <div className={styles.panel}>
      <h3 className={styles.title}>선택 좌석</h3>

      {selectedSeats.length === 0 ? (
        <p className={styles.empty}>
          선택한 좌석이 없습니다.
        </p>
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
      <button className={styles.reserveButton}>
  예매하기
</button>
    </div>
  );
}
