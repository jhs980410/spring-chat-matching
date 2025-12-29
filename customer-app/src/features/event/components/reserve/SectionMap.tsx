import type { Section } from "./types";
import styles from "./SectionMap.module.css";

interface Props {
  sections: Section[];
  selectedSectionId: number | null;
  onSelect: (id: number) => void;
}

export default function SectionMap({
  sections,
  selectedSectionId,
  onSelect,
}: Props) {
  return (
    <div className={styles.wrapper}>
      {sections.map((s) => {
        const isSelected = selectedSectionId === s.sectionId;

        return (
          <button
            key={s.sectionId}
            onClick={() => onSelect(s.sectionId)}
            className={`${styles.sectionButton} ${
              isSelected ? styles.selected : ""
            }`}
          >
            {s.code}구역 ({s.remainSeats})
          </button>
        );
      })}
    </div>
  );
}
