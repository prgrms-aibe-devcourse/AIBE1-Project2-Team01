package org.sunday.projectpop.controller.message;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.sunday.projectpop.model.dto.MessageDto;
import org.sunday.projectpop.model.dto.SuggestionDto;
import org.sunday.projectpop.model.entity.UserAccount;
import org.sunday.projectpop.model.repository.UserAccountRepository;
import org.sunday.projectpop.model.entity.Message;
import org.sunday.projectpop.model.repository.message.MessageRepository;
import org.sunday.projectpop.service.message.MessageService;
import org.sunday.projectpop.model.dto.GetMessageDto;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/message")
public class MessageController {

    private final MessageService messageService;
    private final UserAccountRepository userAccountRepository;
    private final MessageRepository messageRepository;


    @GetMapping
    public String showMessagePage(@RequestParam("userId") String userId, Model model) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        model.addAttribute("user", user);
        model.addAttribute("userId", userId);
        return "message/index";  // templates/message/index.html
    }
    /*
    @GetMapping
public String showMessagePage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
    UserAccount user = userDetails.getUserAccount();
    model.addAttribute("user", user);
    return "message/index";
}
     */


    @PostMapping
    public ResponseEntity<Void> send(
            @RequestBody GetMessageDto dto
    ) {
        UserAccount sender = userAccountRepository.findById(dto.senderId())
                .orElseThrow(() -> new IllegalArgumentException("보내는 유저 없음"));
        UserAccount receiver = userAccountRepository.findById(dto.receiverId())
                .orElseThrow(() -> new IllegalArgumentException("받는 유저 없음"));

        messageService.sendMessage(sender, receiver, dto.message());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sent")
    public ResponseEntity<List<MessageDto>> sent(@RequestParam String senderId) {
        UserAccount sender = userAccountRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
        List<MessageDto> dtoList = messageService.getSentMessages(sender).stream()
                .map(m -> new MessageDto(
                        m.getId(),
                        m.getSender().getUserId(),
                        m.getReceiver().getUserId(),
                        m.getContent(),
                        m.isChecking(),
                        m.getSentAt()
                ))
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/received")
    public ResponseEntity<List<MessageDto>> received(@RequestParam String receiverId) {
        UserAccount receiver = userAccountRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
        List<MessageDto> dtoList = messageService.getReceivedMessages(receiver).stream()
                .map(m -> new MessageDto(
                        m.getId(),
                        m.getSender().getUserId(),
                        m.getReceiver().getUserId(),
                        m.getContent(),
                        m.isChecking(),
                        m.getSentAt()
                ))
                .toList();
        return ResponseEntity.ok(dtoList);
    }
    @PostMapping("/{id}/toggle-read")
    public ResponseEntity<Void> toggleMessageRead(@PathVariable Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));
        message.setChecking(!message.isChecking());
        messageRepository.save(message);
        return ResponseEntity.ok().build();
    }
}