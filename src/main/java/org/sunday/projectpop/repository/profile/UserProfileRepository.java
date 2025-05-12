package org.sunday.projectpop.repository.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunday.projectpop.entity.profile.UserProfile;

import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
}
