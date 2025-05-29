package com.HotelService.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;


@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Hotel extends BaseEntity{
    @Column
    String name;

    @Column
    String address;

    @Column(unique = true)
    String phone;

    @Column(unique = true)
    String email;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column
    String image;

    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY)
    List<Room> rooms;
}
