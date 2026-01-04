package restaurant.management.system.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        HttpSession session = request.getSession();

        if (exception instanceof DisabledException) {
            session.setAttribute("error", "Your account has been disabled.");
            response.sendRedirect("/login?error=disabled");
        } else {
            session.setAttribute("error", "Invalid username or password.");
            response.sendRedirect("/login?error=true");
        }
    }
}
