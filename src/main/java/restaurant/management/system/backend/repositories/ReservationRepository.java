package restaurant.management.system.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restaurant.management.system.backend.entities.Reservation;
import restaurant.management.system.backend.entities.Table;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByCustomerEmail(String email);

    List<Reservation> findByCustomerPhone(String phone);

    // Check for overlapping reservations with either CONFIRMED or PENDING status
    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.table = :table " + "AND ((r.startDateTime BETWEEN :startDateTime AND :endDateTime) " + "OR (r.endDateTime BETWEEN :startDateTime AND :endDateTime)) " + "AND r.status IN :statuses")
    boolean existsByTableAndTimeRangeAndStatus(@Param("table") Table table, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime, @Param("statuses") List<Reservation.Status> statuses);

    Page<Reservation> findByCustomerEmailAndStatusIn(String email, List<Reservation.Status> statuses, Pageable pageable);
    Page<Reservation> findByCustomerPhoneAndStatusIn(String phone, List<Reservation.Status> statuses, Pageable pageable);

    @Query(value = "SELECT COUNT(*) " +
            "FROM reservation r " +
            "JOIN restaurant_table t ON r.table_id = t.id " +
            "WHERE t.restaurant_id = :restaurantId " +
            "AND r.customer_email = :customerEmail " +
            "AND ((r.start_date_time BETWEEN :startDateTime AND :endDateTime) " +
            "OR (r.end_date_time BETWEEN :startDateTime AND :endDateTime)) " +
            "AND r.status IN ('CONFIRMED', 'PENDING')", nativeQuery = true)
    Long countReservationsByCustomerAndRestaurantAndTimeRange(
            @Param("restaurantId") Long restaurantId,
            @Param("customerEmail") String customerEmail,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
    default boolean existsByCustomerAndRestaurantAndTimeRange(Long restaurantId, String customerEmail, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return countReservationsByCustomerAndRestaurantAndTimeRange(restaurantId, customerEmail, startDateTime, endDateTime) > 0;
    }


    @Query(value = "SELECT r " +
            "FROM Reservation r " +
            "JOIN r.table t " +
            "WHERE t.restaurant.id = :restaurantId " +
            "AND (:startDateTime IS NULL OR :endDateTime IS NULL OR " +
            "     (r.startDateTime BETWEEN :startDateTime AND :endDateTime OR " +
            "      r.endDateTime BETWEEN :startDateTime AND :endDateTime)) " +
            "AND (:status IS NULL OR r.status = :status)")
    Page<Reservation> findByRestaurantId(
            @Param("restaurantId") Long restaurantId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("status") String status,
            Pageable pageable);

    @Query(value = "SELECT r " +
            "FROM Reservation r " +
            "JOIN r.table t " +
            "WHERE t.restaurant.id = :restaurantId " +
            "AND t.id = :tableId " +
            "AND (:startDateTime IS NULL OR :endDateTime IS NULL OR " +
            "     (r.startDateTime BETWEEN :startDateTime AND :endDateTime OR " +
            "      r.endDateTime BETWEEN :startDateTime AND :endDateTime)) " +
            "AND (:status IS NULL OR r.status = :status)")
    Page<Reservation> findByRestaurantIdAndTableId(
            @Param("restaurantId") Long restaurantId,
            @Param("tableId")Long tableId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("status") String status,
            Pageable pageable);
}
