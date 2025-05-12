//package org.sunday.projectpop.controller.notification;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.sunday.projectpop.model.dto.NotificationSummaryDto;
//import org.sunday.projectpop.service.message.MessageService;
//import org.sunday.projectpop.service.message.SuggestFromLeaderService;
//
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/notifications")
//public class NotificationController {
//
//    private final SuggestFromLeaderService suggestService;
//    private final MessageService messageService;
//
//    @GetMapping("/summary")
//    public ResponseEntity<NotificationSummaryDto> getSummary(@RequestParam String userId) {
//        var suggestions = suggestService.getReceivedSuggestionsById(userId);
//        var messages = messageService.getUnreadMessagesByReceiver(userId);
//
//        return ResponseEntity.ok(new NotificationSummaryDto(
//                suggestions.stream().map(s -> Map.of("from", s.getSender().getUserId())).toList(),
//                messages.stream().map(m -> Map.of("from", m.getSender().getUserId())).toList()
//        ));
//    }
//}
