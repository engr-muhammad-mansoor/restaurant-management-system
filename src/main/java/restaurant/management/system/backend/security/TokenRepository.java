package restaurant.management.system.backend.security;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Token t WHERE t.userId = :userId")
    void deleteTokensByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Token jt WHERE jt.jwtToken = :jwtToken")
    void deleteByJwtToken(@Param("jwtToken") String jwtToken);

    boolean existsByUserIdAndJwtToken(Long userId, String jwt);

    @Query(value = "SELECT * FROM Token where jwt_token =?1", nativeQuery = true)
    Token findByToken(String tokenId);
}
