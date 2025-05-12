package org.sunday.projectpop.model.repository.message;

import org.sunday.projectpop.model.entity.Message;
import org.sunday.projectpop.model.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiver(UserAccount receiver);
    List<Message> findBySender(UserAccount sender);
}

