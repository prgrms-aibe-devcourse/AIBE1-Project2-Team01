package org.sunday.projectpop.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "user_profile")
@Getter
@Setter
@NoArgsConstructor
public class UserProfile {

    @Id
    @GeneratedValue
    private UUID userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(length = 1000)
    private String bio;

    @Column(length = 255)
    private String profileImageUrl;

    @Column(length = 20)
    private String phone;

    public void update(String nickname, String bio, String profileImageUrl, String phone) {
        this.nickname = nickname;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
        this.phone = phone;
    }
}
