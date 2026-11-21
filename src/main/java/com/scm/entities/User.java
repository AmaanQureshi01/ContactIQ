package com.scm.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "user")
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    private String userId;
    @Column(name = "user_name", nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    private String password;
    @Column(length = 1000)
    private String about;
    @Column(length = 1000)
    private String profilePicture;
    @Column(length = 15)
    private String phoneNumber;
    private String emailToken;
    // information
    private boolean enabled = false;
    private boolean emailVerified = false;
    private boolean phoneVerified = false;

    // SELF, Google, Facebook, Twitter, Linkdin, Github, Microsoft, Apple
    @Enumerated(value = EnumType.STRING)
    private Provider provider = Provider.SELF;
    private String providerId;
    @OneToMany (mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    private List<Contact> contacts= new ArrayList<>();
}
