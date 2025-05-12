package org.sunday.projectpop.service.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.model.entity.Message;
import org.sunday.projectpop.model.entity.UserAccount;
import org.sunday.projectpop.model.repository.message.MessageRepository;
import org.sunday.projectpop.model.repository.message.MessageRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public void sendMessage(UserAccount sender, UserAccount receiver, String content) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        messageRepository.save(message);
    }

    public List<Message> getSentMessages(UserAccount sender) {
        return messageRepository.findBySender(sender);
    }

    public List<Message> getReceivedMessages(UserAccount receiver) {
        return messageRepository.findByReceiver(receiver);
    }
}

