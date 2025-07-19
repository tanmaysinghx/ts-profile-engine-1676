package com.ts.ts_profile_engine_1676.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private String profileId;
    private String emailId;
    private String name;
    private String loginUrl;
    private String message;
}
