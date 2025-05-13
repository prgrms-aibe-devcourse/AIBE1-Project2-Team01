package org.sunday.projectpop.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // ✅ 필수
@AllArgsConstructor
@Table(name = "skill_tag")
@Data
public class SkillTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ← AUTO_INCREMENT
    @Column(name = "tag_id")
    private Long tagId;

    @Column(nullable = false, length = 255, unique = true)
    private String name;
}
