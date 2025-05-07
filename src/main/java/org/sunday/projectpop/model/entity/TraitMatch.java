package org.sunday.projectpop.model.entity;


import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "trait_match")
@Data
public class TraitMatch {
    @Id
    @Column(name = "leader_ocean_key", length = 5)
    private String leaderOceanKey;  // 예: "13245"

    @Column(nullable = false)
    private double openness;
// update rule 별점 star에 대해 h=0.01*star 항목별로 기존 값 *(1-h)+ 새값*h

    @Column(nullable = false)
    private double conscientiousness=3;

    @Column(nullable = false)
    private double extraversion=3;

    @Column(nullable = false)
    private double agreeableness=3;

    @Column(nullable = false)
    private double neuroticism=3;

    @Column(nullable = false)
    private int updated =1;

    @Column(nullable = false)
    private int base =5;

}