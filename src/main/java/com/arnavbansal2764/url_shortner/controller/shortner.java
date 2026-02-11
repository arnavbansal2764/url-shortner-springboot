package com.arnavbansal2764.url_shortner.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


/**
 * REST Controller for URL shortener operations.
 * Handles requests to shorten URLs and retrieve shortened URLs.
 */
@RestController
@RequestMapping("/shortner")
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
    
}
