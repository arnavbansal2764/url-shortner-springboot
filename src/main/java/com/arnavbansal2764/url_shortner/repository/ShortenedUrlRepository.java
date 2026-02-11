package com.arnavbansal2764.url_shortner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arnavbansal2764.url_shortner.entity.ShortenedUrl;
import java.util.Optional;

/**
 * Repository for ShortenedUrl entity.
 * Provides database access methods for shortened URL operations.
 */
@Repository
public interface ShortenedUrlRepository extends JpaRepository<ShortenedUrl, Long> {
    
    /**
     * Find a ShortenedUrl by its short code.
     * 
     * @param shortCode the short code to search for
     * @return Optional containing the ShortenedUrl if found, empty otherwise
     */
    Optional<ShortenedUrl> findByShortCode(String shortCode);
}
