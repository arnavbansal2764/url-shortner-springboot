package com.arnavbansal2764.url_shortner.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.arnavbansal2764.url_shortner.dto.ShortenerRequest;
import com.arnavbansal2764.url_shortner.dto.ShortenerResponse;
import com.arnavbansal2764.url_shortner.service.ShortenerService;
import jakarta.validation.Valid;

import java.util.Optional;
import org.springframework.web.bind.annotation.PutMapping;



/**
 * REST Controller for URL shortener operations.
 * Handles requests to shorten URLs and retrieve shortened URLs.
 */
@RestController
@RequestMapping("/shorten")
public class shortner {
    
    private final ShortenerService shortenerService;
    
    public shortner(ShortenerService shortenerService) {
        this.shortenerService = shortenerService;
    }
    
    @GetMapping()
    public String getMethodName() {
        return "Welcome to shortner send a post request to shorten your url";
    }
    
    @PostMapping()
    public ResponseEntity<ShortenerResponse> postMethodName(@Valid @RequestBody ShortenerRequest request) {
        // URL has been validated by @Valid - starts with http or https
        ShortenerResponse response = shortenerService.shortenUrl(request.getUrl());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{shortCode}")
    public ResponseEntity<ShortenerResponse> getOriginal(@PathVariable String shortCode) {
        Optional<ShortenerResponse> response = shortenerService.getUrlByShortCode(shortCode);
        return response
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Updates the URL for an existing shortened URL.
     * The short code remains the same, but the original URL is updated.
     * Returns 200 OK with the updated data on success, 404 if not found, or 400 if validation fails.
     *
     * @param shortCode the short code of the URL to update
     * @param request the update request containing the new URL
     * @return ResponseEntity with the updated ShortenerResponse, with 200 OK on success,
     *         404 Not Found if the short code doesn't exist, or 400 Bad Request if validation fails
     */
    @PutMapping("/{shortCode}")
    public ResponseEntity<ShortenerResponse> updateShortUrl(@PathVariable String shortCode, @Valid @RequestBody ShortenerRequest request) {
        Optional<ShortenerResponse> response = shortenerService.updateUrlByShortCode(shortCode, request.getUrl());
        return response
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Deletes a shortened URL by its short code.
     * Returns 204 No Content if the short URL was successfully deleted,
     * or 404 Not Found if the short code doesn't exist.
     *
     * @param shortCode the short code of the URL to delete
     * @return ResponseEntity with 204 No Content on success, 404 Not Found if not found
     */
    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteShortUrl(@PathVariable String shortCode) {
        boolean deleted = shortenerService.deleteUrlByShortCode(shortCode);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
