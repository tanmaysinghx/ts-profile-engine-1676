package com.ts.ts_profile_engine_1676.service;

import com.ts.ts_profile_engine_1676.dto.UserProfileRequest;
import com.ts.ts_profile_engine_1676.dto.UserProfileResponse;
import com.ts.ts_profile_engine_1676.entity.User;
import com.ts.ts_profile_engine_1676.entity.UserProfile;
import com.ts.ts_profile_engine_1676.repository.*;
import com.ts.ts_profile_engine_1676.util.ProfileIdGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final BillingRepository billingRepository;
    private final UserRepository managerRepository;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;

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
        managerRepository.findByEmail(request.getManagerEmailId()).orElseThrow(() -> new RuntimeException("Invalid manager email"));

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
        String rawPassword = UUID.randomUUID().toString().substring(0, 8);
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Insert into user table
        User user = User.builder()
                .id(UUID.randomUUID().toString().substring(0, 8))
                .email(request.getEmailId())
                .password(encodedPassword)
                .roleId("0005")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        // Notify via email
        String loginUrl = String.format("http://localhost:1780/auth/login?username=%s&password=%s",
                request.getEmailId(), rawPassword);

        log.info("loginUrl Created for user", loginUrl);
        notificationService.sendWelcomeNotification(request.getEmailId(), rawPassword, loginUrl);

        // Prepare and return response
        return UserProfileResponse.builder()
                .profileId(nextProfileId)
                .emailId(request.getEmailId())
                .name(request.getName())
                .loginUrl(loginUrl)
                .message("User created and notified successfully.")
                .build();
    }
}
