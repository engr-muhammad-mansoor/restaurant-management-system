package restaurant.management.system.backend.services;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import restaurant.management.system.backend.DTOs.ReservationDTO;
import restaurant.management.system.backend.entities.Reservation;
import restaurant.management.system.backend.entities.Table;
import restaurant.management.system.backend.handling.ReservationConflictException;
import restaurant.management.system.backend.handling.ResourceNotFoundException;
import restaurant.management.system.backend.repositories.ReservationRepository;
import restaurant.management.system.backend.repositories.RestaurantRepository;
import restaurant.management.system.backend.repositories.TableRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final EmailService emailService;
    private final RestaurantRepository restaurantRepository;
    private final TableRepository tableRepository;
    // Define the list of statuses to check for (CONFIRMED and PENDING)
    List<Reservation.Status> statuses = Arrays.asList(Reservation.Status.CONFIRMED, Reservation.Status.PENDING);

    public ReservationService(ReservationRepository reservationRepository, EmailService emailService, RestaurantRepository restaurantRepository, TableRepository tableRepository) {
        this.reservationRepository = reservationRepository;
        this.emailService = emailService;
        this.restaurantRepository = restaurantRepository;
        this.tableRepository = tableRepository;
    }

    public ReservationDTO createReservation(ReservationDTO reservationDTO, Table table) {

//        boolean customerConflict = reservationRepository.existsByCustomerAndRestaurantAndTimeRange(table.getRestaurant().getId(), reservationDTO.getCustomerEmail(), reservationDTO.getStartDateTime(), reservationDTO.getEndDateTime());
//
//        if (customerConflict) {
//            throw new ReservationConflictException("You already have a reservation in this restaurant during the specified time range.");
//        }

        boolean exists = reservationRepository.existsByTableAndTimeRangeAndStatus(table, reservationDTO.getStartDateTime(), reservationDTO.getEndDateTime(), statuses);

        if (exists) {
            throw new ReservationConflictException("A reservation already exists for this table during the specified time range.");
        }

        Reservation reservation = reservationDTO.toEntity(reservationDTO, table);
        reservation = reservationRepository.save(reservation);

        emailService.sendReservationEmail(reservation.getCustomerEmail(), reservation.getTable().getId(), reservation.getTable().getRestaurant().getName(), reservation.getStartDateTime(), reservation.getEndDateTime(), reservation.getCustomerName());

        // Return the newly created reservation as DTO
        return ReservationDTO.fromEntity(reservation);
    }

    public Page<ReservationDTO> getReservationsByEmail(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Reservation> reservations = reservationRepository.findByCustomerEmailAndStatusIn(email, statuses, pageable);
        return reservations.map(ReservationDTO::fromEntity);
    }

    public Page<ReservationDTO> getReservationsByMobile(String phone, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Reservation> reservations = reservationRepository.findByCustomerPhoneAndStatusIn(phone, statuses, pageable);
        return reservations.map(ReservationDTO::fromEntity);
    }


    public boolean isNoShow(Reservation reservation) {
        // Calculate if the current time is beyond the reservation time + grace period
        LocalDateTime latestArrivalTime = reservation.getStartDateTime().plusMinutes(reservation.getTable().getNoShowExpiringTime());
        return LocalDateTime.now().isAfter(latestArrivalTime);
    }

    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + reservationId));
        reservation.setStatus(Reservation.Status.CANCELLED);
        reservationRepository.save(reservation);
    }

    public void updateReservation(Reservation reservation) {

        reservation.setStatus(Reservation.Status.CONFIRMED);
        reservation.setPrepaymentStatus(true);
        reservationRepository.save(reservation);
    }

    public String performTransaction(Long reservationId, BigDecimal amount) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + reservationId));
        if (reservation.getStatus().equals(Reservation.Status.CONFIRMED)) {
            throw new ReservationConflictException("Payment already done for this reservation.");
        }
        BigDecimal depositAmount = reservation.getTable().getDepositAmount();
        if (amount.compareTo(depositAmount) < 0) {
            throw new ReservationConflictException("You do not have enough money to perform this reservation");
        }
        updateReservation(reservation);
        return "Reservation done successfully with amount: " + amount + "USD against Table no: " + reservation.getTable().getId() + " of restaurant: " + reservation.getTable().getRestaurant().getName();
    }

    public Page<ReservationDTO> findByRestaurantId(Long restaurantId, LocalDateTime startDateTime, LocalDateTime endDateTime, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Fetch reservations based on optional parameters
        Page<Reservation> reservations = reservationRepository.findByRestaurantId(restaurantId, startDateTime, endDateTime, status, pageable);
        if (reservations.isEmpty()) {
            throw new ResourceNotFoundException("No reservations found for the selected restaurant.");
        }
        // Map Reservation entities to DTOs
        return reservations.map(ReservationDTO::fromEntity);
    }

    public Page<ReservationDTO> findByRestaurantIdAndTableId(Long restaurantId, Long tableId, LocalDateTime startDateTime, LocalDateTime endDateTime, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if(!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found.");
        }
        else if(!tableRepository.existsById(tableId)) {
            throw new ResourceNotFoundException("Table not found.");
        }

        // Fetch reservations based on optional parameters
        Page<Reservation> reservations = reservationRepository.findByRestaurantIdAndTableId(restaurantId,tableId, startDateTime, endDateTime, status, pageable);
        if (reservations.isEmpty()) {
            throw new ResourceNotFoundException("No reservations found for the selected restaurant.");
        }
        // Map Reservation entities to DTOs
        return reservations.map(ReservationDTO::fromEntity);

    }
}
