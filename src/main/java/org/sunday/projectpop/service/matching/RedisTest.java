package org.sunday.projectpop.service.matching;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class RedisTest {

    private final RedisTemplate<String, String> redis;
    private final RedisTagService redisTagService;

    @PostConstruct
    public void run() {
        //  테스트용 태그-유저 세팅
        redisTagService.addUserToTag("u1", 1L);  // tag:1 → u1
        redisTagService.addUserToTag("u1", 2L);  // tag:2 → u1
        redisTagService.addUserToTag("u2", 2L);  // tag:2 → u2
        redisTagService.addUserToTag("u2", 3L);  // tag:3 → u2

        //  태그 2, 3을 동시에 만족하는 유저 찾기
        List<Long> requireTagIds = List.of(2L, 3L);

        try {
            Set<String> result = redisTagService.getRequireMatching(requireTagIds);
            System.out.println("[일치 유저] " + result);
        } catch (NoSuchElementException e) {
            System.out.println("[유저 없음] " + e.getMessage());
        }
    }
}