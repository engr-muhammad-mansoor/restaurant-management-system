package restaurant.management.system.backend.services;


import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import restaurant.management.system.backend.DTOs.UserDTO;
import restaurant.management.system.backend.entities.User;
import restaurant.management.system.backend.entities.UserOtp;
import restaurant.management.system.backend.handling.ResourceNotFoundException;
import restaurant.management.system.backend.repositories.UserOtpRepository;
import restaurant.management.system.backend.repositories.UserRepository;
import restaurant.management.system.backend.security.TokenRepository;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final UserOtpRepository userOtpRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenRepository tokenRepository, EmailService emailService, UserOtpRepository userOtpRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.userOtpRepository = userOtpRepository;
    }

    public UserDTO registerOwner(UserDTO userDTO) {

        if (userRepository.existsByEmailOrPhoneNumber(userDTO.getEmail(), userDTO.getPhoneNumber())) {
            throw new DuplicateKeyException("User with email or mobile number already exists");
        }
        User user = UserDTO.toEntity(userDTO);
        user.setRole(User.Role.OWNER);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setOtpVerified(false);
        user = userRepository.save(user);
        String otp = generateOtp(6);
        UserOtp userOtp = UserOtp.builder().userId(user.getId()).otp(otp).createdOn(LocalDateTime.now()).build();
        userOtpRepository.save(userOtp);
        emailService.sendNewRegisterationEmail(user.getEmail(), user.getName(), otp);
        return UserDTO.fromEntity(user);
    }

    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return UserDTO.fromEntity(user);
    }

    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return UserDTO.fromEntity(user);
    }

    public UserDTO updateOwner(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        if (userRepository.existsByEmailOrPhoneNumberAndIdNot(userDTO.getEmail(), userDTO.getPhoneNumber(), userId)) {
            throw new DuplicateKeyException("User with email or mobile number already exists");
        }
        userDTO.setId(user.getId());
        User updatedUser = UserDTO.toEntity(userDTO);
        updatedUser.setRole(User.Role.OWNER);
        updatedUser.setPassword(user.getPassword());
        updatedUser.setRestaurants(user.getRestaurants());
        updatedUser = userRepository.save(updatedUser);
        return UserDTO.fromEntity(updatedUser);
    }

    public void changePassword(Long userId, Map<String, String> password) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        tokenRepository.deleteTokensByUserId(userId);
        String passKey = passwordEncoder.encode(password.get("password"));
        userRepository.updateUserPassword(userId, passKey);
    }

    public void changePhoneNumber(Long userId, Map<String, String> phoneNumber) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        String updatedPhoneNumber = phoneNumber.get("phoneNumber");
        userRepository.updateUserPhoneNumber(userId, updatedPhoneNumber);
    }

    public void forgetPassword(User user) {
        String email = user.getEmail();
        userOtpRepository.deleteAllByUserId(user.getId());
        // Generate OTP
        String otp = generateOtp(6); // Generates a 6-digit OTP
        UserOtp userOtp = UserOtp.builder().userId(user.getId()).otp(otp).createdOn(LocalDateTime.now()).build();

        userOtpRepository.save(userOtp);
        // Send OTP via email
        try {
            emailService.sendForgetPasswordEmail(email, otp);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    private String generateOtp(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10)); // Append a digit (0-9)
        }
        return otp.toString();
    }

    public void resetPassword(String email, String otp, String password) throws Exception {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("No user exists with this email");
        }
        UserOtp userOtp = userOtpRepository.findByOtp(otp);
        if (userOtp == null) {
            throw new ResourceNotFoundException("Invalid Token");
        }
        LocalDateTime createdOn = userOtp.getCreatedOn();
        LocalDateTime currentTime = LocalDateTime.now();

        if (Duration.between(createdOn, currentTime).toMinutes() > 30) {
            throw new Exception("Token Expired");
        }
        Map map = new LinkedHashMap();
        map.put("password", password);
        changePassword(user.getId(), map);
        user.setOtpVerified(true);
        userRepository.save(user);
        userOtpRepository.deleteAllByUserId(user.getId());
    }

    public void enableAccount(String email, String otp) throws Exception {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("No user exists with this email");
        }
        UserOtp userOtp = userOtpRepository.findByOtp(otp);
        if (userOtp == null) {
            throw new ResourceNotFoundException("Invalid Token");
        }
        LocalDateTime createdOn = userOtp.getCreatedOn();
        LocalDateTime currentTime = LocalDateTime.now();

        if (Duration.between(createdOn, currentTime).toMinutes() > 30) {
            throw new Exception("Token Expired");
        }
        user.setOtpVerified(true);
        userRepository.save(user);
        userOtpRepository.deleteAllByUserId(user.getId());
    }
}
