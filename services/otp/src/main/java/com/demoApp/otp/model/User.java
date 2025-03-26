package com.demoApp.otp.model;

import lombok.*;
import jakarta.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "USERS")

public class User {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "USERNAME")
    private String userName;
}