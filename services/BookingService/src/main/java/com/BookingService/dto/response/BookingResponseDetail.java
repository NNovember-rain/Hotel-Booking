package com.BookingService.dto.response;

import com.BookingService.enums.BookingStatus;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class BookingResponseDetail {
    Long id;
    String fullName; //TODO: FirstName + LastName
    String hotelName;
    String roomType;
    String roomNumber;
    String imageHotel;
    LocalDate checkInDate;
    LocalDate checkOutDate;
    BookingStatus status;
    Double price;
}
