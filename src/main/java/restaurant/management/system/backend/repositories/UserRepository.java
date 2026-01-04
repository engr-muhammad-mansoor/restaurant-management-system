package restaurant.management.system.backend.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restaurant.management.system.backend.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailOrPhoneNumber(String email, String phoneNumber);

    boolean existsByEmailOrPhoneNumberAndIdNot(String email, String phoneNumber, Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :passKey WHERE u.id = :userId")
    void updateUserPassword(@Param("userId") Long userId, @Param("passKey") String passKey);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.phoneNumber = :phoneNumber WHERE u.id = :userId")
    void updateUserPhoneNumber(@Param("userId") Long userId, @Param("phoneNumber") String phoneNumber);

    @Query(value = "select id from user where email=?1", nativeQuery = true)
    Long findIdByEmail(String userEmail);

    User findByPhoneNumber(String phoneNumber);
}
