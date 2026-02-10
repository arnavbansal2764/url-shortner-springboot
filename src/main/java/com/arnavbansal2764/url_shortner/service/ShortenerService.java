package com.arnavbansal2764.url_shortner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.arnavbansal2764.url_shortner.dto.ShortenerResponse;

import java.time.Instant;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

/**
 * Service for URL shortening operations.
 * Generates unique short codes for URLs and manages URL shortening logic.
 */
@Service
public class ShortenerService {
    
    private static final Logger logger = LoggerFactory.getLogger(ShortenerService.class);
    private static final Random random = new Random();
    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_CODE_LENGTH = 6;
    
    /**
     * Generates a short code for the given URL.
     * Uses a hash of the URL (without protocol) combined with random characters
     * to create a unique, randomly-generated short code.
     *
     * @param url the original URL to shorten
     * @return ShortenerResponse containing the original URL, short code, and timestamps
     */
    public ShortenerResponse shortenUrl(String url) {
        try {
            // Remove http:// or https:// from URL for hashing
            String urlWithoutProtocol = url.replaceAll("^https?://", "");
            
            // Generate hash of the URL
            String hashBase = generateHashBase(urlWithoutProtocol);
            
            // Combine hash with random characters to ensure uniqueness
            String shortCode = generateShortCode(hashBase);
            
            // Create timestamps
            Instant now = Instant.now();
            
            logger.info("Generated short code '{}' for URL '{}'", shortCode, url);
            
            return new ShortenerResponse(url, shortCode, now, now);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error generating short code for URL: {}", url, e);
            throw new RuntimeException("Failed to generate short code", e);
        }
    }
    
    /**
     * Generates a hash base from the URL using SHA-256.
     * Takes the first 6 characters of the Base64-encoded hash.
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
    private String generateShortCode(String hashBase) {
        StringBuilder shortCode = new StringBuilder(hashBase);
        
        // Add random characters to reach desired length
        int remainingLength = SHORT_CODE_LENGTH - shortCode.length();
        for (int i = 0; i < remainingLength; i++) {
            shortCode.append(CHARSET.charAt(random.nextInt(CHARSET.length())));
        }
        
        return shortCode.toString();
    }
}
