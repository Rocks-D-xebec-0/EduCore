package com.hem.EduCore.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "books")
public class Book extends BaseEntity{

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int book_id;

@Column(nullable = false)
    private  String title;
    @Column(nullable = false)

    private  String description ;
    @Column(nullable = false)

    private  String publicationYear ;
    @Column(nullable = false)

    private  String pages ;



    @ManyToMany
    @JoinTable(
            name ="book authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();


    @ManyToMany
    @JoinTable(
            name = "book categories",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
private  Set<Category> categories = new HashSet<>();



    @OneToMany( mappedBy = "book")
    private List<Loan> loans=new ArrayList<>();

}
