package com.example.vfms.config;

import java.security.SecureRandom;

public class RandomIdGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomId(int length) {
        StringBuilder RandomId = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            RandomId.append(randomChar);
        }

        return RandomId.toString();
    }

    public static void main(String[] args) {
        int idLength = 10; // Change this to the desired length of your random ID
        String randomId = generateRandomId(idLength);
        System.out.println("Random ID: " + randomId);
    }
}

