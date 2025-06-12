package com.eyedia.eyedia.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<Void> checkHealthStatus() {

        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/health-check")
    public ResponseEntity<String> checkHealth() {

        return ResponseEntity
                .ok()
                .contentType(MediaType.TEXT_HTML)
                .body("<h1>Welcome to Eyedia API</h1>");
    }
}
