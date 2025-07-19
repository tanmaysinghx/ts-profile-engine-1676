package com.ts.ts_profile_engine_1676.repository;

import com.ts.ts_profile_engine_1676.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, String> {

    Optional<UserProfile> findByEmailId(String emailId);

    Optional<UserProfile> findTopByOrderByProfileIdDesc();
}
