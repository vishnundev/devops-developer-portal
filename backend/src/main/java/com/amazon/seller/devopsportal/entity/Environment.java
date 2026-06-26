package com.amazon.seller.devopsportal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "environments")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Environment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    @Column(nullable = false)
    private boolean development;
    @Column(nullable = false)
    private boolean testing;
    @Column(nullable = false)
    private boolean staging;
    @Column(nullable = false)
    private boolean production;
    @Builder.Default
    @ManyToMany(mappedBy = "environments")
    private List<Service> services = new ArrayList<>();
}
