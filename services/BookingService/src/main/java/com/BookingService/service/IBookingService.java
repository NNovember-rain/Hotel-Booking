package com.BookingService.service;

import com.BookingService.dto.request.BookingRequest;
import com.BookingService.dto.response.BookingResponse;
import com.BookingService.dto.response.BookingResponseDetail;
import com.BookingService.entity.Booking;
import com.BookingService.enums.BookingStatus;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IBookingService {
//    void startBooking(BookingRequest request);
    List<BookingResponseDetail> searchBookings(String userId, Long hotelId, Long roomId,
                                               LocalDate checkInDateStart, LocalDate checkOutDateEnd,
                                               BookingStatus status, Double minPrice, Double maxPrice,
                                               Pageable pageable);
    BookingResponseDetail getBooking(Long bookingId);
    String deleteBooking(List<Long> bookingId);
    Booking startBooking(BookingRequest request);
}
