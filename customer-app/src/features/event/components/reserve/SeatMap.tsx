import type { Seat } from "./types";
import styles from "./SeatMap.module.css";

interface Props {
  seats: Seat[];
  selectedSeatIds: number[];
  onSelectSeat: (seatId: number) => void;
}

export default function SeatMap({
  seats,
  selectedSeatIds,
  onSelectSeat,
}: Props) {
  return (
    <div className={styles.grid}>
      {seats.map((seat) => {
        const isSelected = selectedSeatIds.includes(seat.seatId);

        const seatClass =
          seat.status === "SOLD"
            ? styles.sold
            : seat.status === "LOCKED"
            ? styles.locked
            : isSelected
            ? styles.selected
            : styles.available;

        return (
          <div
            key={seat.seatId}
            className={`${styles.seat} ${seatClass}`}
            onClick={() =>
              seat.status === "AVAILABLE" &&
              onSelectSeat(seat.seatId)
            }
            title={`${seat.rowLabel}열 ${seat.seatNumber}번`}
          />
        );
      })}
    </div>
  );
}
