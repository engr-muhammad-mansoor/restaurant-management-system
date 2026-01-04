package restaurant.management.system.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendForgetPasswordEmail(String email, String otp) {
        // Prepare the email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP for password reset is: " + otp + ". Please use it within 30 minutes.");

        // Send the email
        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendReservationEmail(String customerEmail, Long tableId, String restaurantName, LocalDateTime startDateTime, LocalDateTime endDateTime, String customerName) {
        // Format date and time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        String formattedStart = startDateTime.format(formatter);
        String formattedEnd = endDateTime.format(formatter);

        // Prepare email content
        String subject = "Reservation Confirmation at " + restaurantName;
        String message = String.format("Dear %s,\n\n" + "Thank you for your reservation at %s!\n" + "Here are your reservation details:\n" + "- Table ID: %d\n" + "- Start Time: %s\n" + "- End Time: %s\n\n" + "We look forward to serving you!\n\n" + "Best regards,\n" + "%s Team", customerName, restaurantName, tableId, formattedStart, formattedEnd, restaurantName);

        // Send email
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(customerEmail);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);

            mailSender.send(mailMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send reservation email", e);
        }
    }

    public void sendNewRegisterationEmail(String email, String name, String otp) {
        // Prepare the email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("New User OTP");
        message.setText("Hi, Your OTP for verification is: " + otp + ". Please use it within 30 minutes.");

        // Send the email
        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
