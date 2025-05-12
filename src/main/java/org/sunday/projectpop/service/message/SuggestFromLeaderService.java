package org.sunday.projectpop.service.message;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.model.entity.Member;
import org.sunday.projectpop.model.entity.Project;
import org.sunday.projectpop.model.entity.SuggestFromLeader;
import org.sunday.projectpop.model.entity.UserAccount;
import org.sunday.projectpop.model.repository.MemberRepository;
import org.sunday.projectpop.model.repository.message.SuggestFromLeaderRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SuggestFromLeaderService {

    private final SuggestFromLeaderRepository repository;
    private final MemberRepository memberRepository;

    public void suggest(Project project, UserAccount sender, UserAccount receiver, String message) {
        SuggestFromLeader suggest = new SuggestFromLeader();
        suggest.setProject(project);
        suggest.setSender(sender);
        suggest.setReceiver(receiver);
        suggest.setMessage(message);
        suggest.setCreatedAt(LocalDateTime.now());
        repository.save(suggest);
    }

    @Transactional
    public void accept(Long suggestId) {
        SuggestFromLeader suggest = repository.findById(suggestId)
                .orElseThrow(() -> new RuntimeException("제안 없음"));

        Member member = new Member();
        member.setProject(suggest.getProject());
        member.setUser(suggest.getReceiver());

        memberRepository.save(member);
        repository.delete(suggest);  // 수락 후 삭제
    }

    public List<SuggestFromLeader> getReceivedSuggestions(UserAccount receiver) {
        return repository.findByReceiver(receiver);
    }


    public List<SuggestFromLeader> getSentSuggestions(UserAccount sender) {
        return repository.findBySender(sender);
    }

    public void reject(Long id) {
        SuggestFromLeader suggestion = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("제안 없음"));
        repository.delete(suggestion); // 또는 상태 변경
    }


}
