import type { Seat } from "./types";
import styles from "./SeatMap.module.css";

interface Props {
  seats: Seat[];
  onSelectSeat: (seatId: number) => void;
}

export default function SeatMap({ seats, onSelectSeat }: Props) {
  return (
    <div className={styles.grid}>
      {seats.map((seat) => {
        const seatClass =
          seat.status === "AVAILABLE"
            ? styles.available
            : seat.status === "SOLD"
            ? styles.sold
            : styles.selected;

        return (
          <div
            key={seat.id}
            className={`${styles.seat} ${seatClass}`}
            onClick={() =>
              seat.status !== "SOLD" && onSelectSeat(seat.id)
            }
            title={`${seat.row}열 ${seat.number}번`}
          />
        );
      })}
    </div>
  );
}
