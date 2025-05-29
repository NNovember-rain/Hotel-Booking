package com.HistoryService.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingHistoryRespone {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status;
    private String hotelName;
    private String roomNumber;
    private double price;
}
