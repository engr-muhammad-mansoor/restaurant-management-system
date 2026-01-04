package restaurant.management.system.backend.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import restaurant.management.system.backend.DTOs.UserDTO;
import restaurant.management.system.backend.entities.User;
import restaurant.management.system.backend.handling.ResourceNotFoundException;
import restaurant.management.system.backend.repositories.UserRepository;
import restaurant.management.system.backend.security.AuthenticationRequest;
import restaurant.management.system.backend.security.AuthenticationService;
import restaurant.management.system.backend.security.GenericResponse;
import restaurant.management.system.backend.security.TokenRepository;
import restaurant.management.system.backend.services.UserService;
import restaurant.management.system.backend.utils.ApiResponse;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    public AuthController(UserService userService, AuthenticationService authenticationService, TokenRepository tokenRepository, UserRepository userRepository) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ApiResponse<UserDTO> registerOwner(@RequestBody UserDTO userDTO) {
        return new ApiResponse<>(true, "User registered successfully. Please check your email for otp.", userService.registerOwner(userDTO));
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @GetMapping("/user")
    public ApiResponse<UserDTO> getOwner(@RequestParam String email) {
        return new ApiResponse<>(true, "User fetched successfully", userService.getUserByEmail(email));
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @GetMapping("/user/{userId}")
    public ApiResponse<UserDTO> getOwnerById(@PathVariable Long userId) {
        return new ApiResponse<>(true, "User fetched successfully", userService.getUserById(userId));
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @PutMapping("/user/{userId}")
    public ApiResponse<UserDTO> updateOwner(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
        return new ApiResponse<>(true, "User updated successfully", userService.updateOwner(userId, userDTO));
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @PutMapping("/change-password/{userId}")
    public ApiResponse<UserDTO> changePassword(@PathVariable Long userId, @RequestBody Map<String, String> password) {
        userService.changePassword(userId, password);
        return new ApiResponse<>(true, "Password updated successfully", null);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<GenericResponse> authenticate(@RequestBody AuthenticationRequest request) throws BadRequestException {

        var response = GenericResponse.builder().message("Login ok").status(HttpStatus.OK.name()).data(authenticationService.authenticate(request)).build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String token, HttpSession session, HttpServletResponse response) {

        Map<String, String> responseMessage = new HashMap<>();

        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7); // Remove "Bearer " prefix
            tokenRepository.deleteByJwtToken(jwtToken); // Invalidate JWT in the database
            SecurityContextHolder.clearContext(); // Clear security context

            responseMessage.put("message", "Logout successful.");
        }
        // 2. Handle session-based logout
        session.invalidate();

        // Prevent caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        responseMessage.put("sessionMessage", "Logout successful.");
        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping("/token-for-google-user")
    public ResponseEntity<GenericResponse> getTokenForGoogleUser() throws BadRequestException {
        try {
            var response = GenericResponse.builder().message("Login ok").status(HttpStatus.OK.name()).data(authenticationService.getGoogleUserResponse()).build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            var response = GenericResponse.builder().message("Unauthorized access").status(HttpStatus.UNAUTHORIZED.name()).data(null).build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @PutMapping("/change-phone-number/{userId}")
    public ApiResponse<UserDTO> changePhoneNumber(@PathVariable Long userId, @RequestBody Map<String, String> password) {
        userService.changePhoneNumber(userId, password);
        return new ApiResponse<>(true, "Phone Number updated successfully", null);
    }

    @PutMapping("/forget-password")
    public ApiResponse<UserDTO> forgetPassword(@RequestParam(required = false) String email, @RequestParam(required = false) String phoneNumber) throws Exception {
        User user = null;
        if (email == null && phoneNumber == null) {
            throw new Exception("Minimum one parameter is required");
        } else if (phoneNumber == null) {
            user = userRepository.findByEmail(email);
        } else if (email == null) {
            user = userRepository.findByPhoneNumber(phoneNumber);
        }
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        userService.forgetPassword(user);
        return new ApiResponse<>(true, "Otp sent successfully", null);
    }

    @PutMapping("/reset-password")
    public ApiResponse<UserDTO> resetPassword(String email, @RequestParam String otp, @RequestParam String password) throws Exception {

        userService.resetPassword(email, otp, password);
        return new ApiResponse<>(true, "Password updated successfully", null);
    }

    @PutMapping("/activate-account")
    public ApiResponse<UserDTO> enableAccount(String email, @RequestParam String otp) throws Exception {

        userService.enableAccount(email, otp);
        return new ApiResponse<>(true, "User activated successfully", null);
    }


}
