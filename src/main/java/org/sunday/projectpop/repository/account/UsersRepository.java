package org.sunday.projectpop.repository.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunday.projectpop.entity.account.Users;

import java.util.Optional;
import java.util.UUID;

public interface UsersRepository extends JpaRepository<Users, UUID> {
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);
}