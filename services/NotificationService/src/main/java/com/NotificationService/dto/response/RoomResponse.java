package com.NotificationService.dto.response;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class RoomResponse {
    Long id;
    String roomNumber;
    String description;
    double price;
    HotelNameResponse hotel;
}