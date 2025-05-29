package com.HistoryService.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingHistorySavedEvent {
    private Long bookingId;
    private Long profileId;
    private String userId;
    private String status;
    private String hotelName;
    private String roomNumber;
    private double price;
}
