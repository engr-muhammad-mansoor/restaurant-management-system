package restaurant.management.system.backend.DTOs;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import restaurant.management.system.backend.entities.Reservation;
import restaurant.management.system.backend.entities.Table;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private LocalDateTime startDateTime; // Reservation start time
    private LocalDateTime endDateTime;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String status;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean prepaymentStatus;
    private Long tableId;

    public static ReservationDTO fromEntity(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setCustomerName(reservation.getCustomerName());
        dto.setCustomerEmail(reservation.getCustomerEmail());
        dto.setCustomerPhone(reservation.getCustomerPhone());
        dto.setStartDateTime(reservation.getStartDateTime());
        dto.setEndDateTime(reservation.getEndDateTime());
        dto.setStatus(reservation.getStatus().name());
        dto.setPrepaymentStatus(reservation.isPrepaymentStatus());
        dto.setTableId(reservation.getTable().getId());
        return dto;
    }

    public Reservation toEntity(ReservationDTO reservationDTO, Table table) {
        Reservation reservation = new Reservation();
        reservation.setId(reservationDTO.id);
        reservation.setCustomerName(reservationDTO.customerName);
        reservation.setCustomerEmail(reservationDTO.customerEmail);
        reservation.setCustomerPhone(reservationDTO.customerPhone);
        reservation.setStartDateTime(reservationDTO.startDateTime);
        reservation.setEndDateTime(reservationDTO.endDateTime);
        reservation.setTable(table);
        return reservation;
    }
}
