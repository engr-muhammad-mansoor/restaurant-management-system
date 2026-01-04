package restaurant.management.system.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import restaurant.management.system.backend.entities.User;
import restaurant.management.system.backend.repositories.UserRepository;

import java.io.IOException;
import java.util.Objects;

@Component
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    @Lazy
    private AuthenticationService authenticationService;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        var oauth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String authorizedClientRegistrationId = oauth2AuthenticationToken.getAuthorizedClientRegistrationId();
        var oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");

        User user = new User();

        if (authorizedClientRegistrationId.equalsIgnoreCase("google")) {
            user.setEmail(Objects.requireNonNull(oauthUser.getAttribute("email")).toString());
            user.setName(Objects.requireNonNull(oauthUser.getAttribute("name")).toString());
            user.setRole(User.Role.OWNER);
            user.setOtpVerified(true);
            String token;
            String role;
            AuthenticationResponse googleUserResponse;
            String redirectUrl = "http://localhost:4200/auth/succeed";
            if (!userRepository.existsByEmail(email)) {
                userRepository.save(user);
                googleUserResponse = authenticationService.getGoogleUserResponse();
                token = googleUserResponse.getAccessToken();
                role = String.valueOf(User.Role.OWNER);
            } else {
                googleUserResponse = authenticationService.getGoogleUserResponse();
                token = googleUserResponse.getAccessToken();
                role = String.valueOf(User.Role.OWNER);
            }
            response.sendRedirect(redirectUrl + "?thepa=" + token + "&kirdaar=" + role);
        }
    }
}