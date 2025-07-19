package com.ts.ts_profile_engine_1676.service;

import com.ts.ts_profile_engine_1676.dto.RegisterRequestDto;
import com.ts.ts_profile_engine_1676.dto.UserProfileRequest;
import com.ts.ts_profile_engine_1676.dto.UserProfileResponse;
import com.ts.ts_profile_engine_1676.entity.UserProfile;
import com.ts.ts_profile_engine_1676.repository.*;
import com.ts.ts_profile_engine_1676.util.ProfileIdGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final GroupRepository groupRepository;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final BillingRepository billingRepository;
    private final NotificationService notificationService;

    @Transactional
    public UserProfileResponse generateUserProfile(UserProfileRequest request) {

        // Email uniqueness check
        userProfileRepository.findByEmailId(request.getEmailId()).ifPresent(u -> {
            throw new RuntimeException("Email already exists in user_profile");
        });

        // Validate all foreign keys
        groupRepository.findById(request.getGroupId()).orElseThrow(() -> new RuntimeException("Invalid groupId"));
        projectRepository.findById(request.getProjectId()).orElseThrow(() -> new RuntimeException("Invalid projectId"));
        teamRepository.findById(request.getTeamId()).orElseThrow(() -> new RuntimeException("Invalid teamId"));
        billingRepository.findById(request.getBillingId()).orElseThrow(() -> new RuntimeException("Invalid billingId"));
//        managerRepository.findByEmail(request.getManagerEmailId()).orElseThrow(() -> new RuntimeException("Invalid manager email"));

        // Generate next profile ID
        String lastProfileId = userProfileRepository.findTopByOrderByProfileIdDesc()
                .map(UserProfile::getProfileId)
                .orElse(null);
        String nextProfileId = ProfileIdGenerator.generateNextProfileId(lastProfileId);

        // Insert into user_profile
        UserProfile userProfile = UserProfile.builder()
                .profileId(nextProfileId)
                .name(request.getName())
                .emailId(request.getEmailId())
                .groupId(request.getGroupId())
                .projectId(request.getProjectId())
                .teamId(request.getTeamId())
                .billingId(request.getBillingId())
                .managerEmailId(request.getManagerEmailId())
                .role("user")
                .createdAt(LocalDateTime.now())
                .build();

        userProfileRepository.save(userProfile);

        // Generate password
        String rawPassword = generateSecurePassword(10);

        // Call Register Url
        RestTemplate restTemplate = new RestTemplate();

        RegisterRequestDto registerPayload = RegisterRequestDto.builder()
                .email(request.getEmailId())
                .password(rawPassword)
                .roleName("user")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequestDto> entity = new HttpEntity<>(registerPayload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "http://localhost:1625/v2/api/auth/register",
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("User registered successfully.");
            } else {
                log.error("Failed to register user: " + response.getBody());
                throw new RuntimeException("Auth service returned non-success status");
            }
        } catch (Exception e) {
            log.error("Error while calling auth service", e);
            throw new RuntimeException("Failed to register user via auth service", e);
        }


        log.debug("User Table Entry Ends");

        // Notify via email
        String loginUrl = String.format("http://localhost:1780/auth/login?username=%s&password=%s",
                request.getEmailId(), rawPassword);

        log.info("loginUrl Created for user", loginUrl);
        notificationService.sendWelcomeNotification(request.getEmailId(), loginUrl);

        // Prepare and return response
        return UserProfileResponse.builder()
                .profileId(nextProfileId)
                .emailId(request.getEmailId())
                .name(request.getName())
                .loginUrl(loginUrl)
                .message("User created and notified successfully.")
                .build();
    }

    public String generateSecurePassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%!&*";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
    }
}
