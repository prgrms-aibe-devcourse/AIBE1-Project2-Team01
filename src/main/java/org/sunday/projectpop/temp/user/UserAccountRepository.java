package org.sunday.projectpop.temp.user;



import org.sunday.projectpop.project.model.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
}
