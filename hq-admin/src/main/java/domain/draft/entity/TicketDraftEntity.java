package domain.draft.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_draft")
@Getter
public class TicketDraftEntity {

    @Id
    private Long id;

    @Column(name = "event_draft_id")
    private Long eventDraftId;

    private String name;
    private int price;

    @Column(name = "total_quantity")
    private int totalQuantity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
