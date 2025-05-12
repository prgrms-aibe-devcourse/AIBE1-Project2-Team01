package org.sunday.projectpop.newnew.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunday.projectpop.newnew.entity.UserProfile;

import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
}
