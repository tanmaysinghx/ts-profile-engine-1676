package com.ts.ts_profile_engine_1676.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @Column(name = "profile_id", nullable = false, unique = true)
    private String profileId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email_id", nullable = false, unique = true)
    private String emailId;

    @Column(name = "group_id", nullable = false)
    private String groupId;

    @Column(name = "project_id", nullable = false)
    private String projectId;

    @Column(name = "team_id", nullable = false)
    private String teamId;

    @Column(name = "billing_id", nullable = false)
    private String billingId;

    @Column(name = "manager_email_id", nullable = false)
    private String managerEmailId;

    @Column(name = "role", nullable = false)
    private String role = "user";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}