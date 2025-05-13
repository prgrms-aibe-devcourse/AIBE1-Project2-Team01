package org.sunday.projectpop.project.model.entity;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserAccount user;

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