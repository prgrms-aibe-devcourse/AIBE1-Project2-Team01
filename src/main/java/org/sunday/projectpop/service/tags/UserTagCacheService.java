package org.sunday.projectpop.service.tags;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.dto.tags.UserTagCacheDTO;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserTagCacheService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String TAG_KEY_PREFIX = "user:tags:";
    private static final Duration TTL = Duration.ofDays(7); // 캐시 유효기간

    public void saveTags(UserTagCacheDTO dto) {
        try {
            String key = TAG_KEY_PREFIX + dto.userId();
            String value = objectMapper.writeValueAsString(dto);
            redisTemplate.opsForValue().set(key, value, TTL);
            log.info("✅ 태그 캐시 저장 완료 - key: {}", key);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("태그 직렬화 실패", e);
        }
    }

    public Optional<UserTagCacheDTO> getTags(UUID userId) {
        String key = TAG_KEY_PREFIX + userId;
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) return Optional.empty();

        try {
            return Optional.of(objectMapper.readValue(json, UserTagCacheDTO.class));
        } catch (JsonProcessingException e) {
            log.error("❌ 태그 역직렬화 실패 - key: {}", key);
            return Optional.empty();
        }
    }

    public void deleteTags(UUID userId) {
        String key = TAG_KEY_PREFIX + userId;
        redisTemplate.delete(key);
        log.info("🗑️ 태그 캐시 삭제 완료 - key: {}", key);
    }
}