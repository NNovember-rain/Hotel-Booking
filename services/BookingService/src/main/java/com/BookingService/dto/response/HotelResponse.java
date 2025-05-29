package com.BookingService.dto.response;

import lombok.Data;

@Data
public class HotelResponse {
    Long id;
    String name;
    String address;
    String phone;
    String email;
    String description;
    String image;
}
