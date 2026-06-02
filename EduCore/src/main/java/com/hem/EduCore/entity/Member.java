package com.hem.EduCore.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Table(name = "members")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Member extends BaseEntity{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int member_id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column
    private LocalDate memberShipDate;


    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Loan> loans;
}
