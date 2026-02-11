package com.arnavbansal2764.url_shortner.service;

import org.springframework.stereotype.Service;

import java.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Service for generating short codes.
 * Responsible for creating unique short codes using hashing and random character generation.
 */
@Service
public class ShortCodeGeneratorService {
    
    private static final Random random = new Random();
    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_CODE_LENGTH = 6;
    
    /**
     * Generates a unique short code for the given URL.
     * Uses a hash of the URL (without protocol) combined with random characters
     * to create a unique, randomly-generated short code.
     *
     * @param url the original URL to generate a short code for
     * @return a unique short code
     */
    public String generateShortCode(String url) throws NoSuchAlgorithmException {
        // Remove http:// or https:// from URL for hashing
        String urlWithoutProtocol = url.replaceAll("^https?://", "");
        
        // Generate hash base
        String hashBase = generateHashBase(urlWithoutProtocol);
        
        // Combine hash with random characters to ensure uniqueness
        return combineHashWithRandomChars(hashBase);
    }
    
    /**
     * Generates a hash base from the URL using SHA-256.
     * Takes the first 4 characters of the Base64-encoded hash.
     *
     * @param input the URL string to hash
     * @return a hash-based string used as the foundation for the short code
     * @throws NoSuchAlgorithmException if SHA-256 algorithm is not available
     */
    private String generateHashBase(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(input.getBytes());
        String base64Hash = Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);
        
        // Take first 4 characters of the hash
        return base64Hash.substring(0, Math.min(4, base64Hash.length()));
    }
    
    /**
     * Generates a random short code of fixed length.
     * Combines hash-based characters with random characters for uniqueness and randomness.
     *
     * @param hashBase the hash-based prefix for the short code
     * @return a unique random short code
     */
    private String combineHashWithRandomChars(String hashBase) {
        StringBuilder shortCode = new StringBuilder(hashBase);
        
        // Add random characters to reach desired length
        int remainingLength = SHORT_CODE_LENGTH - shortCode.length();
        for (int i = 0; i < remainingLength; i++) {
            shortCode.append(CHARSET.charAt(random.nextInt(CHARSET.length())));
        }
        
        return shortCode.toString();
    }
}
