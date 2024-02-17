package com.cloudbees.interview.ticket.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity health() {
        log.info("System is up and running");
        return ResponseEntity.ok().body("System is up and running");
    }
}
