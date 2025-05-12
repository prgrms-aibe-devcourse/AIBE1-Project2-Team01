package org.sunday.projectpop.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunday.projectpop.model.entity.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, String > {
}
