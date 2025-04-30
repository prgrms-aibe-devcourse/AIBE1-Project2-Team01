package org.sunday.projectpop.model.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "trait_match")
@Data
public class TraitMatch {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String traitType;  // "O", "C", "E", "A", "N"

    @Column(nullable = false)
    private int leaderValue;   // 1~5

    @Column(nullable = false)
    private int followerValue; // 1~5
}
