package com.BookingService.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomStatusEvent {
    private String requestId;
    private Long idBooking;
    private Long roomId;
    private Long profileId;
    private String userId;
    private Long hotelId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status; // "available" or "booked"
    private Double price;
    private String email;
    private String fullName;
    private String roomNumber;
    private String hotelName;
    private String username;
}
