package org.sunday.projectpop.service.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunday.projectpop.dto.profile.UserProfileRequestDTO;
import org.sunday.projectpop.dto.profile.UserProfileResponseDTO;
import org.sunday.projectpop.model.entity.UserProfile;
import org.sunday.projectpop.model.entity.Users;
import org.sunday.projectpop.model.repository.UserProfileRepository;
import org.sunday.projectpop.model.repository.UsersRepository;

import java.util.Optional;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UsersRepository usersRepository;
    @Transactional
    public void saveProfile(UUID userId, UserProfileRequestDTO dto) {
        if (userProfileRepository.existsById(userId)) {
            throw new IllegalStateException("이미 프로필이 존재합니다.");
        }
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("사용자 정보를 찾을 수 없습니다."));
        // ✅ 이메일도 반영
        if (dto.email() != null && !dto.email().isBlank()) {
            user.setEmail(dto.email());
            usersRepository.save(user);
        }
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setNickname(dto.nickname());
        profile.setBio(dto.bio());
        profile.setProfileImageUrl(dto.profileImageUrl());
        profile.setPhone(dto.phone());
        userProfileRepository.save(profile);
    }
    public Optional<UserProfileResponseDTO> getProfile(UUID userId) {
        return userProfileRepository.findById(userId)
                .map(profile -> new UserProfileResponseDTO(
                        profile.getUserId(),
                        profile.getUser().getEmail(),
                        profile.getNickname(),
                        profile.getBio(),
                        profile.getProfileImageUrl(),
                        profile.getPhone()
                ));
    }
    public void deleteProfile(UUID userId) {
        userProfileRepository.deleteById(userId);
    }
    public Optional<UserProfileResponseDTO> getProfileByEmail(String email) {
        return usersRepository.findByEmail(email)
                .flatMap(user -> getProfile(user.getId()));
    }
    public void updateProfileByEmail(String email, UserProfileRequestDTO dto) {
        usersRepository.findByEmail(email).ifPresent(user -> {
            // ✅ 이메일이 바뀌었는지 체크하고 업데이트
            if (dto.email() != null && !dto.email().isBlank() && !dto.email().equals(user.getEmail())) {
                user.setEmail(dto.email());
                usersRepository.save(user); // 👈 이메일도 반영
            }
            userProfileRepository.findById(user.getId()).ifPresentOrElse(profile -> {
                profile.update(dto.nickname(), dto.bio(), dto.profileImageUrl(), dto.phone());
                userProfileRepository.save(profile);
            }, () -> {
                saveProfile(user.getId(), dto);
            });
        });
    }
}