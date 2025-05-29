package com.HotelService.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RoomRequest {
    String roomNumber;
    String type;
    String description;
    double price;
    String status;
    Long hotelId;
    MultipartFile image;
}
