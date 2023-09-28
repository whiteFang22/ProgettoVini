package com.example.classes;

import java.security.SecureRandom;
import java.math.BigInteger;

public class AuthCodeGenerator {
    public static void main(String[] args) {
        // Generate a random auth code
        String authCode = generateAuthCode();
        
        // Print the generated auth code
        System.out.println("Generated Auth Code: " + authCode);
    }

    public static String generateAuthCode() {
        // Define the length of the auth code (you can adjust this as needed)
        int codeLength = 8; // For example, an 8-character auth code
        
        // Create a secure random number generator
        SecureRandom random = new SecureRandom();
        
        // Generate a random byte array
        byte[] randomBytes = new byte[codeLength];
        random.nextBytes(randomBytes);
        
        // Convert the byte array to a hexadecimal string
        String authCode = new BigInteger(1, randomBytes).toString(16);
        
        // Ensure the auth code has the desired length by padding with zeros if necessary
        while (authCode.length() < codeLength) {
            authCode = "0" + authCode;
        }
        
        return authCode;
    }
}
