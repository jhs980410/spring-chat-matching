package domain.manager.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_manager")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketManager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private ManagerStatus status;
    @Column(nullable = false)
    private LocalDateTime createdAt;

}
