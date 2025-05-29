package com.HotelService.dto.response;

import lombok.Data;

@Data
public class RoomResponse {
    Long id;
    String roomNumber;
    String type;
    String description;
    double price;
    String status;
    String image;
}
