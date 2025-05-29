package com.HotelService.entity;

import com.HotelService.enums.RoomStatus;
import com.HotelService.enums.RoomType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Room extends BaseEntity{

    @Column
    String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType type;

    @Column
    String description;

    @Column(nullable = false)
    double price;

    @Column
    String image;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status;

    @ManyToOne
    Hotel hotel; // Many rooms can belong to one hotel

}
