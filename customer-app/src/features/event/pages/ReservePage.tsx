import { useState } from "react";
import styles from "../components/reserve/ReservePage.module.css";

import { sections } from "../components/reserve/sectionDummy";
import { seats as seatDummy } from "../components/reserve/seatDummy";
import type { Seat } from "../components/reserve/types";

import SectionMap from "../components/reserve/SectionMap";
import SeatMap from "../components/reserve/SeatMap";
import ReserveSummaryPanel from "../components/reserve/ReserveSummaryPanel";

export default function ReservePage() {
  const [selectedSectionId, setSelectedSectionId] =
    useState<number | null>(null);

  const [seatList, setSeatList] =
    useState<Seat[]>(seatDummy);

  const [selectedSeats, setSelectedSeats] =
    useState<Seat[]>([]);

  const selectedSection = sections.find(
    (s) => s.id === selectedSectionId
  );

  const handleSeatSelect = (seatId: number) => {
    const seat = seatList.find((s) => s.id === seatId);
    if (!seat) return;

    // 이미 선택된 좌석 해제
    if (selectedSeats.some((s) => s.id === seatId)) {
      setSeatList((prev) =>
        prev.map((s) =>
          s.id === seatId ? { ...s, status: "AVAILABLE" } : s
        )
      );
      setSelectedSeats((prev) =>
        prev.filter((s) => s.id !== seatId)
      );
      return;
    }

    // 최대 선택 제한
    if (selectedSeats.length >= 4) {
      alert("최대 4좌석까지 선택 가능합니다.");
      return;
    }

    // 선택 처리
    setSeatList((prev) =>
      prev.map((s) =>
        s.id === seatId ? { ...s, status: "SELECTED" } : s
      )
    );

    setSelectedSeats((prev) => [...prev, seat]);
  };

  return (
    <div className={styles.wrapper}>
      <div className={styles.left}>
        <div className={styles.stage}>STAGE</div>

        <SectionMap
          sections={sections}
          selectedSectionId={selectedSectionId}
          onSelect={setSelectedSectionId}
        />

        {selectedSectionId && (
          <SeatMap
            seats={seatList.filter(
              (s) => s.sectionId === selectedSectionId
            )}
            onSelectSeat={handleSeatSelect}
          />
        )}
      </div>

      <div className={styles.right}>
        <ReserveSummaryPanel
          selectedSeats={selectedSeats}
          price={selectedSection?.ticketPrice ?? 0}
        />
      </div>
    </div>
  );
}
