package com.BookingService.entity;

import com.BookingService.enums.BookingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking extends BaseEntity{
    @Column
    Long profileId;

    @Column(nullable = false)
    String userId;

    @Column(nullable = false)
    Long hotelId;

    @Column(nullable = false)
    Long roomId;

    LocalDate checkInDate;
    LocalDate checkOutDate;

    @Enumerated(EnumType.STRING)
    @Column
    BookingStatus status;

    @Column(nullable = false)
    Double price;

}
