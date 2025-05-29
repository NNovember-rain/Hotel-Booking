package com.ProfileService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ProfileService.entity.Profile;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    // Custom query methods can be defined here if needed

    Optional<Profile> findByUserId(String userId);
}
