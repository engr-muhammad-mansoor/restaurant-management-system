package restaurant.management.system.backend.security;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import restaurant.management.system.backend.entities.User;
import restaurant.management.system.backend.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;


    public AuthenticationResponse authenticate(AuthenticationRequest request) throws BadRequestException {
        User person = userRepository.findByEmail(request.getEmail());
        if (person == null) {
            throw new BadRequestException("User not found with email.");
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        var UserDetails = userService.loadUserByUsername(request.getEmail());

        var jwtToken = jwtService.generateToken(UserDetails);
        tokenRepository.save(Token.builder().tokenType(String.valueOf(TokenType.JWT)).jwtToken(jwtToken.getToken()).userId(person.getId()).build());

        return AuthenticationResponse.builder().email(UserDetails.getUsername()).name(person.getName()).accessToken(jwtToken.getToken()).role(String.valueOf(person.getRole())).userId(person.getId()).otpVerified(person.isOtpVerified()).build();
    }

    public AuthenticationResponse authenticateWithGoogleSuccessfulResponse(String email) {
        try{
        var userDetails = userService.loadUserByUsername(email);
        var jwtToken = jwtService.generateToken(userDetails);
        User person = userRepository.findByEmail(email);
        tokenRepository.save(Token.builder().tokenType(String.valueOf(TokenType.JWT)).jwtToken(jwtToken.getToken()).userId(person.getId()).build());

        return AuthenticationResponse.builder().email(email).name(person.getName()).role(String.valueOf(person.getRole())).userId(person.getId()).accessToken(jwtToken.getToken()).otpVerified(person.isOtpVerified()).build();}
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public AuthenticationResponse getGoogleUserResponse() {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();

        try {
            Long logggedInUserId = getCurrentUserId();
            if (logggedInUserId == null) {
                return authenticationResponse;
            }
            User person = userRepository.findById(logggedInUserId).get();
            authenticationResponse = authenticateWithGoogleSuccessfulResponse(person.getEmail());
            return authenticationResponse;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return authenticationResponse;
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        String email = null;

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            email = ((OAuth2User) principal).getAttribute("email");
        }
        if (email != null) {
            User user = userRepository.findByEmail(email);
            return user != null ? user.getId() : null;
        }
        return null;
    }
}
