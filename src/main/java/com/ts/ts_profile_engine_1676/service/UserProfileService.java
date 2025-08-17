package com.ts.ts_profile_engine_1676.service;

import com.ts.ts_profile_engine_1676.dto.RegisterRequestDto;
import com.ts.ts_profile_engine_1676.dto.UserProfileGetResponse;
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
import java.util.*;

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

        userProfileRepository.findByEmailId(request.getEmailId()).ifPresent(u -> {
            throw new RuntimeException("Email already exists in user_profile");
        });

        groupRepository.findById(request.getGroupId()).orElseThrow(() -> new RuntimeException("Invalid groupId"));
        projectRepository.findById(request.getProjectId()).orElseThrow(() -> new RuntimeException("Invalid projectId"));
        teamRepository.findById(request.getTeamId()).orElseThrow(() -> new RuntimeException("Invalid teamId"));
        billingRepository.findById(request.getBillingId()).orElseThrow(() -> new RuntimeException("Invalid billingId"));
//        managerRepository.findByEmail(request.getManagerEmailId()).orElseThrow(() -> new RuntimeException("Invalid manager email"));

        String lastProfileId = userProfileRepository.findTopByOrderByProfileIdDesc()
                .map(UserProfile::getProfileId)
                .orElse(null);
        String nextProfileId = ProfileIdGenerator.generateNextProfileId(lastProfileId);

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

        String rawPassword = generateSecurePassword(10);

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

        String loginUrl = String.format("http://localhost:1780/auth/login?username=%s&password=%s",
                request.getEmailId(), rawPassword);

        log.info("loginUrl Created for user", loginUrl);
        notificationService.sendWelcomeNotification(request.getEmailId(), loginUrl);

        return UserProfileResponse.builder()
                .profileId(nextProfileId)
                .emailId(request.getEmailId())
                .name(request.getName())
                .loginUrl(loginUrl)
                .message("User created and notified successfully.")
                .status("success")
                .timestamp(LocalDateTime.now())
                .errorCode(null)
                .requestId("REQ0001")
                .build();
    }

    public static String generateSecurePassword(int length) {
        if (length < 12) {
            length = 12;
        }

        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "@#$%!&*";
        String allChars = upper + lower + digits + special;

        SecureRandom random = new SecureRandom();
        List<Character> passwordChars = new ArrayList<>();

        passwordChars.add(upper.charAt(random.nextInt(upper.length())));
        passwordChars.add(lower.charAt(random.nextInt(lower.length())));
        passwordChars.add(digits.charAt(random.nextInt(digits.length())));
        passwordChars.add(special.charAt(random.nextInt(special.length())));

        for (int i = passwordChars.size(); i < length; i++) {
            passwordChars.add(allChars.charAt(random.nextInt(allChars.length())));
        }

        Collections.shuffle(passwordChars, random);
        StringBuilder password = new StringBuilder();

        for (char c : passwordChars) {
            password.append(c);
        }

        return password.toString();
    }

    @Transactional
    public UserProfileGetResponse getUserProfile(String userEmail) {
        Optional<UserProfile> userProfileOpt = userProfileRepository.findByEmailId(userEmail);
        if (userProfileOpt.isEmpty()) {
            return UserProfileGetResponse.builder()
                    .status("failure")
                    .message("User not found.")
                    .errorCode("ERR00001")
                    .timestamp(LocalDateTime.now())
                    .requestId("REQ0002")
                    .build();
        }
        UserProfile userProfile = userProfileOpt.get();
        return UserProfileGetResponse.builder()
                .message("User found.")
                .status("success")
                .timestamp(LocalDateTime.now())
                .requestId("REQ0002")
                .data(userProfile)
                .build();
    }
}
