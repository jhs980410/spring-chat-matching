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
        const isSelected = selectedSectionId === s.id;

        return (
          <button
            key={s.id}
            onClick={() => onSelect(s.id)}
            className={`${styles.sectionButton} ${
              isSelected ? styles.selected : ""
            }`}
          >
            {s.floor} {s.code}구역
          </button>
        );
      })}
    </div>
  );
}
