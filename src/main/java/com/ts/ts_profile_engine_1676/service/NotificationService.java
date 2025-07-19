package com.ts.ts_profile_engine_1676.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final RestTemplate restTemplate;

    public void sendWelcomeNotification(String email, String rawPassword, String url) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", rawPassword);
        body.put("url", url);

        try {
            restTemplate.postForObject("http://localhost:1689/v2/api/notifications/send", body, String.class);
        } catch (Exception e) {
            log.error("Failed to send notification for {}", email, e);
        }
    }
}
