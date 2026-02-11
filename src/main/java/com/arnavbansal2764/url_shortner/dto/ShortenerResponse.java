package com.arnavbansal2764.url_shortner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

/**
 * DTO for URL shortener response.
 * Contains the original URL, generated short code, ID, timestamps, and access count.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortenerResponse {
    
    private Long id;
    private String url;
    private String shortCode;
    private Instant createdAt;
    private Instant updatedAt;
    private Long accessCount;
}
