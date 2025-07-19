package com.ts.ts_profile_engine_1676.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserProfileRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String emailId;

    @NotBlank(message = "GroupId is required")
    private String groupId;

    @NotBlank(message = "ProjectId is required")
    private String projectId;

    @NotBlank(message = "TeamId is required")
    private String teamId;

    @NotBlank(message = "BillingId is required")
    private String billingId;

    @NotBlank(message = "Manager email is required")
    @Email(message = "Invalid manager email format")
    private String managerEmailId;
}