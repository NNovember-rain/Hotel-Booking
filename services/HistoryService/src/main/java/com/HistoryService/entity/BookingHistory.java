package com.HistoryService.entity;

import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingHistory extends BaseEntity {
    private Long bookingId;

    private Long profileId;

    private String userId;

    private Long hotelId;

    private Long roomId;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private String status;
    private String hotelName;
    private String roomNumber;
    private double price;
}
