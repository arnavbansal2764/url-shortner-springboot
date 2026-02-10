package com.arnavbansal2764.url_shortner.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class main {
    
    @GetMapping
    public String welcome() {
        return "Welcome to URL Shortener";
    }
    
}
