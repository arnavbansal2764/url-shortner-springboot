package com.arnavbansal2764.url_shortner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.arnavbansal2764.url_shortner.dto.ShortenerResponse;
import com.arnavbansal2764.url_shortner.entity.ShortenedUrl;
import com.arnavbansal2764.url_shortner.repository.ShortenedUrlRepository;

import java.time.Instant;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

/**
 * Service for managing URL shortening operations.
 * Handles creation, retrieval, and updates of shortened URLs.
 * Delegates short code generation to ShortCodeGeneratorService.
 */
@Service
public class ShortenerService {
    
    private static final Logger logger = LoggerFactory.getLogger(ShortenerService.class);
    
    private final ShortenedUrlRepository shortenedUrlRepository;
    private final ShortCodeGeneratorService shortCodeGeneratorService;
    
    /**
     * Constructor injection of dependencies.
     * Follows Spring Boot best practices for dependency injection.
     */
    public ShortenerService(ShortenedUrlRepository shortenedUrlRepository, ShortCodeGeneratorService shortCodeGeneratorService) {
        this.shortenedUrlRepository = shortenedUrlRepository;
        this.shortCodeGeneratorService = shortCodeGeneratorService;
    }
    
    /**
     * Generates a short code for the given URL, saves it to the database, and returns the response.
     *
     * @param url the original URL to shorten
     * @return ShortenerResponse containing the original URL, short code, ID, and timestamps
     */
    public ShortenerResponse shortenUrl(String url) {
        try {
            // Generate short code using the generator service
            String shortCode = shortCodeGeneratorService.generateShortCode(url);
            
            // Create timestamps
            Instant now = Instant.now();
            
            // Create and save the entity to the database
            ShortenedUrl shortenedUrl = new ShortenedUrl(null, url, shortCode, now, now, 0L);
            ShortenedUrl savedUrl = shortenedUrlRepository.save(shortenedUrl);
            
            logger.info("Generated short code '{}' with ID '{}' for URL '{}'", shortCode, savedUrl.getId(), url);
            
            return new ShortenerResponse(savedUrl.getId(), savedUrl.getUrl(), savedUrl.getShortCode(), savedUrl.getCreatedAt(), savedUrl.getUpdatedAt(), savedUrl.getAccessCount());
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error generating short code for URL: {}", url, e);
            throw new RuntimeException("Failed to generate short code", e);
        }
    }
    
    /**
     * Retrieves the original URL by its short code and increments its access count.
     * Updates the access count in the database and returns the URL with the updated counter.
     * 
     * @param shortCode the short code to retrieve
     * @return Optional containing the ShortenerResponse with updated access count if found, empty otherwise
     */
    public Optional<ShortenerResponse> getUrlByShortCode(String shortCode) {
        return shortenedUrlRepository.findByShortCode(shortCode)
            .map(url -> {
                // Increment access count and update timestamp
                url.setAccessCount(url.getAccessCount() + 1);
                url.setUpdatedAt(Instant.now());
                
                // Save the updated entity
                ShortenedUrl updatedUrl = shortenedUrlRepository.save(url);
                
                logger.info("URL retrieved with short code '{}'. Access count: {}", shortCode, updatedUrl.getAccessCount());
                
                // Return response with updated access count
                return new ShortenerResponse(updatedUrl.getId(), updatedUrl.getUrl(), updatedUrl.getShortCode(), updatedUrl.getCreatedAt(), updatedUrl.getUpdatedAt(), updatedUrl.getAccessCount());
            });
    }
    
    /**
     * Updates the URL for an existing short code.
     * Finds the shortened URL by short code and updates its original URL.
     * The updatedAt timestamp is automatically updated.
     * 
     * @param shortCode the short code of the URL to update
     * @param newUrl the new original URL
     * @return Optional containing the updated ShortenerResponse if found, empty otherwise
     */
    public Optional<ShortenerResponse> updateUrlByShortCode(String shortCode, String newUrl) {
        return shortenedUrlRepository.findByShortCode(shortCode)
            .map(url -> {
                // Update the URL and timestamp
                url.setUrl(newUrl);
                url.setUpdatedAt(Instant.now());
                
                // Save the updated entity
                ShortenedUrl updatedUrl = shortenedUrlRepository.save(url);
                
                logger.info("Updated URL for short code '{}'. New URL: '{}'", shortCode, newUrl);
                
                // Return response with updated data
                return new ShortenerResponse(updatedUrl.getId(), updatedUrl.getUrl(), updatedUrl.getShortCode(), updatedUrl.getCreatedAt(), updatedUrl.getUpdatedAt(), updatedUrl.getAccessCount());
            });
    }
    
    /**
     * Retrieves statistics for a shortened URL by its short code without incrementing access count.
     * Unlike getUrlByShortCode, this method does NOT increment the access counter.
     * Used for retrieving stats about a URL without affecting its access count.
     * 
     * @param shortCode the short code to retrieve stats for
     * @return Optional containing the ShortenerResponse if found, empty otherwise
     */
    public Optional<ShortenerResponse> getStatsByShortCode(String shortCode) {
        return shortenedUrlRepository.findByShortCode(shortCode)
            .map(url -> {
                logger.info("Retrieved stats for short code '{}'. Current access count: {}", shortCode, url.getAccessCount());
                
                // Return response without modifying the entity
                return new ShortenerResponse(url.getId(), url.getUrl(), url.getShortCode(), url.getCreatedAt(), url.getUpdatedAt(), url.getAccessCount());
            });
    }
    
    /**
     * Deletes a shortened URL by its short code.
     * Removes the entry from the database.
     * 
     * @param shortCode the short code of the URL to delete
     * @return true if the URL was found and deleted, false if not found
     */
    public boolean deleteUrlByShortCode(String shortCode) {
        return shortenedUrlRepository.findByShortCode(shortCode)
            .map(url -> {
                // Delete the URL from the database
                shortenedUrlRepository.delete(url);
                logger.info("Deleted shortened URL with short code '{}' and ID '{}'.", shortCode, url.getId());
                return true;
            })
            .orElse(false);
    }
}
