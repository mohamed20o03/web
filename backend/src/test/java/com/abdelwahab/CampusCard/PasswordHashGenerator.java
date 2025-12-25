package com.abdelwahab.CampusCard;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "password123";
        String hash = encoder.encode(password);
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);
        
        // Verify it works
        boolean matches = encoder.matches(password, hash);
        System.out.println("Verification: " + matches);
    }
}
