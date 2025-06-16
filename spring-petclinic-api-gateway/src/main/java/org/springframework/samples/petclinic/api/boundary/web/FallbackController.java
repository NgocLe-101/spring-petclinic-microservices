package org.springframework.samples.petclinic.api.boundary.web;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @PostMapping("/fallback")
    public ResponseEntity<String> fallbackPost() {
        return ResponseEntity.status(HttpStatus.SC_SERVICE_UNAVAILABLE)
                .body("Chat is currently unavailable. Please try again later.");
    }

    @GetMapping("/fallback")
    public ResponseEntity<String> fallbackGet() {
        return ResponseEntity.status(HttpStatus.SC_SERVICE_UNAVAILABLE)
                .body("Service is currently unavailable. Please try again later.");
    }
}
