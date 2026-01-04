package restaurant.management.system.backend.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restaurant.management.system.backend.entities.Restaurant;
import restaurant.management.system.backend.entities.User;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Page<Restaurant> findByOwner(User owner, Pageable pageable);      // Support for owner-specific restaurants with pagination

    Page<Restaurant> findByActive(boolean active, Pageable pageable); // Support for active restaurants with pagination

    @Query("SELECT r FROM Restaurant r " + "WHERE r.active = true " + "  AND (:keyword IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " + "  OR (:keyword IS NULL OR LOWER(r.address) LIKE LOWER(CONCAT('%', :keyword, '%'))) " + "  OR (:keyword IS NULL OR LOWER(r.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Restaurant> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByNameOrPhoneNumber(String name, String phoneNumber);
}
