package com.HotelService.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingResponse {
    private String bookingId;
    private String message;
    private String status;
}
