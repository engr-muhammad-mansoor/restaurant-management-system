package restaurant.management.system.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;

    private String customerEmail;

    private String customerPhone;

    private LocalDateTime startDateTime; // Reservation start time
    private LocalDateTime endDateTime;   // Reservation end time

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private Table table;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    private boolean prepaymentStatus = false;

    @PrePersist
    public void setEndDateTimeIfNotSet() {
        if (this.endDateTime == null) {
            // If end time is not set, use default duration (e.g., 2 hours)
            this.endDateTime = this.startDateTime.plusHours(2);
        }
    }

    public enum Status {
        PENDING, CONFIRMED, CANCELLED
    }
}
