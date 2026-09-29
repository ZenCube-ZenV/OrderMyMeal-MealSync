package com.ordermymeal.auth.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String hash = encoder.encode("Admin@123");

        System.out.println(hash);
        System.out.println(
                encoder.matches("Admin@123", hash));
    }
}
