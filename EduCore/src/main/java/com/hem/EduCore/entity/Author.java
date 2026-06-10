package com.hem.EduCore.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "authors")
public class Author extends BaseEntity{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authorId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String bio;

    private boolean isDeleted = false;


    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>();

}
