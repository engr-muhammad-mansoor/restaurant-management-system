package restaurant.management.system.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository jwtTokenRepository;
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        jwt = authHeader.substring(7);
        String tokenId = "";
        try {
            Jwt parse = Jwts.parserBuilder().setSigningKey(secretKey).build().parse(jwt);
            Object header = parse.getHeader();
            Claims body = (Claims) parse.getBody();

            //get jti
            tokenId = body.getId();
        } catch (Exception e) {
            return;
        }
        //Get The JWT
        Token storedToken = jwtTokenRepository.findByToken(tokenId);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            jwtTokenRepository.save(storedToken);
            SecurityContextHolder.clearContext();
        }
    }
}

