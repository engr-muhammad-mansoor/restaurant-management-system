package restaurant.management.system.backend.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restaurant.management.system.backend.entities.Reservation;
import restaurant.management.system.backend.entities.Restaurant;
import restaurant.management.system.backend.entities.Table;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TableRepository extends JpaRepository<Table, Long> {
    Page<Table> findByRestaurant(Restaurant restaurant, Pageable pageable); // Support for pagination

    @Query(value = """
    SELECT t
    FROM Table t
    WHERE t.restaurant = :restaurant
      AND NOT EXISTS (
          SELECT 1
          FROM Reservation r
          WHERE r.table = t
            AND r.status IN (:statuses)
            AND (
                (:startDateTime BETWEEN r.startDateTime AND r.endDateTime)
                OR (:endDateTime BETWEEN r.startDateTime AND r.endDateTime)
                OR (r.startDateTime BETWEEN :startDateTime AND :endDateTime)
            )
      )
""")
    Page<Table> findUnreservedTablesByRestaurant(
            @Param("restaurant") Restaurant restaurant,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("statuses") List<Reservation.Status> statuses, // Pass enums as a list
            Pageable pageable
    );

}
