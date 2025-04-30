package org.sunday.projectpop.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tag")
@Data
public class Tag {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ← AUTO_INCREMENT
    @Column(name = "tag_id")
    private Long tagId;

    @Column(nullable = false, length = 255)
    private String name;
}
