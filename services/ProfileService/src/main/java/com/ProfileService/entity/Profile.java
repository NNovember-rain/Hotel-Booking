package com.ProfileService.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.persistence.Id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long profileId;

    @Column
    String userId;

    String email;

    String username;

    String firstName;

    String lastName;

    @Column
    LocalDate dob;
}
