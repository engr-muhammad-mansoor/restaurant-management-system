package restaurant.management.system.backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import restaurant.management.system.backend.DTOs.ReservationDTO;
import restaurant.management.system.backend.entities.Table;
import restaurant.management.system.backend.handling.ResourceNotFoundException;
import restaurant.management.system.backend.services.ReservationService;
import restaurant.management.system.backend.services.TableService;
import restaurant.management.system.backend.utils.ApiResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final TableService tableService;

    public ReservationController(ReservationService reservationService, TableService tableService) {
        this.reservationService = reservationService;
        this.tableService = tableService;
    }

    @PostMapping
    public ApiResponse<ReservationDTO> createReservation(@RequestBody ReservationDTO reservationDTO) {
        Table table = tableService.findById(reservationDTO.getTableId()).orElseThrow(() -> new ResourceNotFoundException("Table not found with ID: " + reservationDTO.getTableId()));

//        if (reservationDTO.getEndDateTime() == null) {
//            reservationDTO.setEndDateTime(reservationDTO.getStartDateTime().plusHours(table.getDefaultDurationInHours()));
//        }

        if (reservationDTO.getEndDateTime() == null) {
            reservationDTO.setEndDateTime(reservationDTO.getStartDateTime().plusHours(2));
        }

        // Save the reservation
        ReservationDTO createdReservation = reservationService.createReservation(reservationDTO, table);

        return new ApiResponse<>(true, "Reservation created successfully", createdReservation);
    }

    @GetMapping
    public ApiResponse<Page<ReservationDTO>> getReservationsByEmailOrMobile(@RequestParam(required = false) String email, @RequestParam(required = false) String mobile, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        if (email != null && !email.isEmpty()) {
            Page<ReservationDTO> reservationsByEmail = reservationService.getReservationsByEmail(email, page, size);
            return new ApiResponse<>(true, "Fetched reservations successfully by email", reservationsByEmail);
        }

        if (mobile != null && !mobile.isEmpty()) {
            Page<ReservationDTO> reservationsByMobile = reservationService.getReservationsByMobile(mobile, page, size);
            return new ApiResponse<>(true, "Fetched reservations successfully by mobile", reservationsByMobile);
        }

        return new ApiResponse<>(false, "Please provide either email or mobile to search for reservations", null);
    }


    @PutMapping
    public ApiResponse<?> cancelReservation(@RequestParam Long reservationId) {

        reservationService.cancelReservation(reservationId);
        return new ApiResponse<>(true, "Reservation cancelled successfully", null);
    }

    @PostMapping("/payment")
    public ApiResponse<?> performPayment(@RequestParam Long reservationId, @RequestParam BigDecimal amount) {

        return new ApiResponse<>(true, "Payment performed successfully", reservationService.performTransaction(reservationId, amount));
    }

    @GetMapping("/{restaurantId}")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public ApiResponse<Page<ReservationDTO>> getReservationsByRestaurantId(@PathVariable Long restaurantId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) LocalDateTime startDateTime, @RequestParam(required = false) LocalDateTime endDateTime, @RequestParam(required = false) String status) {

        Page<ReservationDTO> reservations = reservationService.findByRestaurantId(restaurantId, startDateTime, endDateTime, status, page, size);

        return new ApiResponse<>(true, "Reservations fetched for the restaurant successfully", reservations);
    }

    @GetMapping("/{restaurantId}/{tableId}")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public ApiResponse<Page<ReservationDTO>> getReservationsByRestaurantIdAndTableId(@PathVariable Long restaurantId, @PathVariable Long tableId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) LocalDateTime startDateTime, @RequestParam(required = false) LocalDateTime endDateTime, @RequestParam(required = false) String status) {

        Page<ReservationDTO> reservations = reservationService.findByRestaurantIdAndTableId(restaurantId, tableId, startDateTime, endDateTime, status, page, size);

        return new ApiResponse<>(true, "Reservations fetched for the restaurant successfully", reservations);
    }

}
