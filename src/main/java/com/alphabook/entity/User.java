package com.alphabook.entity;

import com.alphabook.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author Hoang Van Khoi
 * @date 4/6/2026
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private LocalDate dob;

    @Column(nullable = false)

    private String phoneNumber;

    private String address;

    private String province;

    private String district;

    private String village;


    @Enumerated(EnumType.STRING)
    private Role role;

}

