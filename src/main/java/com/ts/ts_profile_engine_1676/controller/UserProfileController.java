package com.ts.ts_profile_engine_1676.controller;

import com.ts.ts_profile_engine_1676.dto.UserProfileRequest;
import com.ts.ts_profile_engine_1676.dto.UserProfileResponse;
import com.ts.ts_profile_engine_1676.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/profile-engine")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping("/generate-user")
    public ResponseEntity<UserProfileResponse> generateUser(@RequestBody @Valid UserProfileRequest request) {
        UserProfileResponse response = userProfileService.generateUserProfile(request);
        return ResponseEntity.ok(response);
    }
}
