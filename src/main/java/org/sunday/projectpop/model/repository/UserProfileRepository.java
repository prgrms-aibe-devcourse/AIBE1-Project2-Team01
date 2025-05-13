
package org.sunday.projectpop.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunday.projectpop.model.entity.UserProfile;

import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
}