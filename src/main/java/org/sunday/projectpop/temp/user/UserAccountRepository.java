package org.sunday.projectpop.temp.user;



import org.sunday.projectpop.project.model.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
//공고 생성을 위해 임시 구현
public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
}
