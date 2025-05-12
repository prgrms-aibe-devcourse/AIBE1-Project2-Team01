package org.sunday.projectpop.model.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_trait")
@Data
public class UserTrait {

    @Id
    @Column(name = "user_id", length = 36)
    private String userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId  // userId = user.userId 로 매핑
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @Column(nullable = false)
    private int openness;

    @Column(nullable = false)
    private int conscientiousness;

    @Column(nullable = false)
    private int extraversion;

    @Column(nullable = false)
    private int agreeableness;

    @Column(nullable = false)
    private int neuroticism;
}