package org.sunday.projectpop.project.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunday.projectpop.project.model.entity.UserProfile;

import java.util.Optional;


public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUser_UserId(String userId);
}
