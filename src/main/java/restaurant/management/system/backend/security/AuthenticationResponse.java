package restaurant.management.system.backend.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    public Long userId;
    public String email;
    public String name;
    public String role;
    private String accessToken;
    private boolean otpVerified;
}
