package org.sunday.projectpop.model.entity;

import jakarta.persistence.*;
import lombok.Data;



@Entity
@Table(name = "user_tag", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "tag_id"}))
@Data
public class UserTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;
}