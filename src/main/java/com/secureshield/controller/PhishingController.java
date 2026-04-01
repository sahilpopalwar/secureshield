package com.secureshield.controller;

import com.secureshield.service.PhishingDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/phishing")
@PreAuthorize("isAuthenticated()")
public class PhishingController {

    @Autowired
    private PhishingDetectionService phishingService;

    @PostMapping("/check")
    public ResponseEntity<PhishingDetectionService.PhishingResult> checkUrl(@RequestBody CheckUrlRequest request) {
        var result = phishingService.checkUrl(request.getUrl());
        return ResponseEntity.ok(result);
    }

    static class CheckUrlRequest {
        private String url;

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }
}
