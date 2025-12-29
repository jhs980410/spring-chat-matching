import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import styles from "../components/reserve/ReservePage.module.css";
import api from "../../../api/axios";

import SectionMap from "../components/reserve/SectionMap";
import SeatMap from "../components/reserve/SeatMap";
import ReserveSummaryPanel from "../components/reserve/ReserveSummaryPanel";

import type { Section, Seat } from "../components/reserve/types";

export default function ReservePage() {
  const { id } = useParams<{ id: string }>();
  const eventId = Number(id);

  const [sections, setSections] = useState<Section[]>([]);
  const [selectedSectionId, setSelectedSectionId] =
    useState<number | null>(null);

  const [selectedSeatIds, setSelectedSeatIds] = useState<number[]>([]);

  // ✅ seatId → 라벨 매핑 (요약 패널용)
  const [seatLabelMap, setSeatLabelMap] = useState<
    Record<number, { rowLabel: string; seatNumber: number }>
  >({});

  /** 🔹 좌석 데이터 로드 */
  useEffect(() => {
    api.get(`/events/${eventId}/seats`).then((res) => {
      const sectionsData: Section[] = res.data;
      setSections(sectionsData);

      // ✅ 좌석 라벨 맵 생성 (정확한 필드명 사용)
      const map: Record<number, { rowLabel: string; seatNumber: number }> = {};
      sectionsData.forEach((section) => {
        section.seats.forEach((seat: Seat) => {
          map[seat.seatId] = {
            rowLabel: seat.rowLabel,
            seatNumber: seat.seatNumber,
          };
        });
      });
      setSeatLabelMap(map);
    });
  }, [eventId]);

  // ✅ sectionId 기준으로 선택 섹션 찾기
  const selectedSection = sections.find(
    (s) => s.sectionId === selectedSectionId
  );

  /** 🔹 좌석 선택 / 해제 */
  const handleSeatSelect = (seatId: number) => {
    setSelectedSeatIds((prev) => {
      if (prev.includes(seatId)) {
        return prev.filter((id) => id !== seatId);
      }
      if (prev.length >= 4) {
        alert("최대 4좌석까지 선택 가능합니다.");
        return prev;
      }
      return [...prev, seatId];
    });
  };

  return (
    <div className={styles.wrapper}>
      <div className={styles.left}>
        <div className={styles.stage}>STAGE</div>

        {/* ✅ SectionMap 필드명 정합 */}
        <SectionMap
          sections={sections}
          selectedSectionId={selectedSectionId}
          onSelect={(sectionId) => {
            setSelectedSectionId(sectionId);
            setSelectedSeatIds([]); // 🔥 구역 변경 시 좌석 초기화
          }}
        />

        {selectedSection && (
          <SeatMap
            seats={selectedSection.seats}
            selectedSeatIds={selectedSeatIds}
            onSelectSeat={handleSeatSelect}
          />
        )}
      </div>

      <div className={styles.right}>
        <ReserveSummaryPanel
          selectedSeatIds={selectedSeatIds}
          seatLabelMap={seatLabelMap}
          price={selectedSection?.price ?? 0} // ✅ ticketPrice ❌ → price ⭕
        />
      </div>
    </div>
  );
}
