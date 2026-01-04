package restaurant.management.system.backend.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restaurant.management.system.backend.entities.UserOtp;

@Repository
public interface UserOtpRepository extends JpaRepository<UserOtp, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM UserOtp u WHERE u.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    UserOtp findByOtp(String otp);
}
