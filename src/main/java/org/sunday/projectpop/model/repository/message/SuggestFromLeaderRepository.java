package org.sunday.projectpop.model.repository.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunday.projectpop.model.entity.SuggestFromLeader;
import org.sunday.projectpop.model.entity.UserAccount;

import java.util.List;

public interface SuggestFromLeaderRepository extends JpaRepository<SuggestFromLeader, Long> {
    List<SuggestFromLeader> findByReceiver(UserAccount receiver);

    // SuggestFromLeaderRepository.java
    List<SuggestFromLeader> findBySender(UserAccount sender);

}
