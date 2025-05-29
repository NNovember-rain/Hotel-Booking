package com.NotificationService.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationEvent {
    private Long idBooking;
    private Long profileId;
    private String userId;
    private String type;
    private String message;
    private String roomNumber;
    private String hotelName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Double price;
    private String email;
    private String fullName;
}
