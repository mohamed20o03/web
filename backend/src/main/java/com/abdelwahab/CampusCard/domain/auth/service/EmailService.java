package com.abdelwahab.CampusCard.domain.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails.
 * 
 * Handles email verification and other email notifications.
 * 
 * Configuration required in application.properties:
 * - spring.mail.host
 * - spring.mail.port
 * - spring.mail.username
 * - spring.mail.password
 * - app.frontend.url
 */
@Service
public class EmailService {
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    @Value("${spring.mail.username:noreply@campuscard.com}")
    private String fromEmail;
    
    /**
     * Send email verification to user.
     * 
     * @param toEmail User's email address
     * @param userId User ID for verification
     * @param token Verification token
     */
    public void sendVerificationEmail(String toEmail, Integer userId, String token) {
        if (mailSender == null) {
            System.out.println("Email sending disabled - mailSender not configured");
            return;
        }
        
        String verificationLink = String.format(
            "%s/verify?token=%s&userId=%d", 
            frontendUrl, token, userId
        );
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("CampusCard - Email Verification Required");
        message.setText(String.format(
            "Dear Student,\n\n" +
            "Thank you for registering with CampusCard.\n\n" +
            "Please verify your email address by clicking the link below:\n\n" +
            "%s\n\n" +
            "This link will expire in 24 hours.\n\n" +
            "If you did not register for CampusCard, please ignore this email.\n\n" +
            "Best regards,\n" +
            "CampusCard Team\n" +
            "Port Said University - Faculty of Engineering",
            verificationLink
        ));
        
        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email: " + e.getMessage());
        }
    }
    
    /**
     * Send approval notification to user.
     * 
     * @param toEmail User's email address
     * @param firstName User's first name
     */
    public void sendApprovalEmail(String toEmail, String firstName) {
        if (mailSender == null) {
            System.out.println("Email sending disabled - mailSender not configured");
            return;
        }
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("CampusCard - Account Approved");
        message.setText(String.format(
            "Dear %s,\n\n" +
            "Congratulations! Your CampusCard account has been approved.\n\n" +
            "You can now log in and access all features.\n\n" +
            "Login here: %s/login\n\n" +
            "Best regards,\n" +
            "CampusCard Team",
            firstName, frontendUrl
        ));
        
        try {
            mailSender.send(message);
        } catch (Exception e) {
            // Log error but don't throw - approval already completed
            System.err.println("Failed to send approval email: " + e.getMessage());
        }
    }
    
    /**
     * Send rejection notification to user.
     * 
     * @if (mailSender == null) {
            System.out.println("Email sending disabled - mailSender not configured");
            return;
        }
        
        param toEmail User's email address
     * @param firstName User's first name
     * @param reason Rejection reason
     */
    public void sendRejectionEmail(String toEmail, String firstName, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("CampusCard - Account Status Update");
        message.setText(String.format(
            "Dear %s,\n\n" +
            "Unfortunately, your CampusCard registration has not been approved.\n\n" +
            "Reason: %s\n\n" +
            "If you believe this is an error or need assistance, please contact the administration.\n\n" +
            "Best regards,\n" +
            "CampusCard Team",
            firstName, reason
        ));
        
        try {
            mailSender.send(message);
        } catch (Exception e) {
            // Log error but don't throw
            System.err.println("Failed to send rejection email: " + e.getMessage());
        }
    }
}
