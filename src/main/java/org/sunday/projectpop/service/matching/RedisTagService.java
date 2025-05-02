package org.sunday.projectpop.service.matching;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.model.repository.UserSkillTagRepository;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;


/**
 * Redis 기반
 * user에게 tag 추가
 * user에게 tag 제거
 * 일치하는 tag set*/

@Service
@RequiredArgsConstructor
public class RedisTagService {
    private final UserSkillTagRepository userSkillTagRepository;


    // 레디스 기반 맵
    private final RedisTemplate<String,String> redis;
    // 레디스 tag 맵에 user 추가
    public void addUserToTag(@Param("userId")String userId,@Param("tagId")Long tagId){
        String key="tag:"+tagId;
        redis.opsForSet().add(key,userId);
    }
    // tag에 맞는 user 가져오기
    public Set<String> getUsersByTag(@Param("tagId")Long tagId){
        return redis.opsForSet().members("tag:"+tagId);
    }
    // tag에서 user 지우기
    public void removeUserFromTag(@Param("userId")String userId,@Param("tagId")Long tagId){
        redis.opsForSet().remove("tag:"+tagId,userId);
    }

    // 일치하는 user set
    public HashSet<String> getRequireMatching(List<Long> requireList){

        if (requireList == null || requireList.isEmpty()) {
            throw new IllegalArgumentException("요구 태그가 비어 있습니다.");
        }

        HashSet<String> memberSet = null;

        for (Long requireId : requireList) {
            Set<String> current = redis.opsForSet().members("tag:" + requireId);

            if (current == null || current.isEmpty()) {
                String tagName= userSkillTagRepository.findNameByTagId(requireId);
                throw new NoSuchElementException("요구 조건 " + tagName + "에 해당하는 유저가 없습니다.");
            }

            if (memberSet == null) {
                memberSet = new HashSet<>(current);  // 최초 초기화
            } else {
                memberSet.retainAll(current);  // 교집합 계산
                if (memberSet.isEmpty()) {
                    throw new NoSuchElementException("요구 조건 전체를 만족하는 유저가 없습니다.");
                }
            }
        }

        return memberSet;
    }


    // 성향 맵 구성

}
