package com.arnavbansal2764.url_shortner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arnavbansal2764.url_shortner.entity.ShortenedUrl;

/**
 * Repository for ShortenedUrl entity.
 * Provides database access methods for shortened URL operations.
 */
@Repository
public interface ShortenedUrlRepository extends JpaRepository<ShortenedUrl, Long> {
}
