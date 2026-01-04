package restaurant.management.system.backend.security;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Token {

    @Id
    @GeneratedValue
    public Integer id;

    @Column(unique = true)
    public String jwtToken;

    public String tokenType = String.valueOf(TokenType.JWT);

    public boolean revoked;

    public boolean expired;

    public Long userId;
}
