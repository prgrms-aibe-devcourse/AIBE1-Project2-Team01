package org.sunday.projectpop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunday.projectpop.entity.UserProfile;

import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
}
