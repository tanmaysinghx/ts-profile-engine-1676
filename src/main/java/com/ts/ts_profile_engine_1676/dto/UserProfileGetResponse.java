package com.ts.ts_profile_engine_1676.dto;

import com.ts.ts_profile_engine_1676.entity.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileGetResponse {
    private String message;
    private String status;
    private LocalDateTime timestamp;
    private String errorCode;
    private String requestId;
    private UserProfile data;
}
