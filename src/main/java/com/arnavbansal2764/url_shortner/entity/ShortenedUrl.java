package com.arnavbansal2764.url_shortner.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

/**
 * Entity representing a shortened URL.
 * Stores the original URL and its corresponding short code in the database.
 */
@Entity
@Table(name = "shortened_urls")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortenedUrl {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String url;
    private String shortCode;
    private Instant createdAt;
    private Instant updatedAt;
    private Long accessCount = 0L;
}
