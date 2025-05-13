package org.sunday.projectpop.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_field")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false, unique = true)
    private String name;

    @Column(length = 255)
    private String description;
}
