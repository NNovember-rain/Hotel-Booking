package com.HistoryService.service;

import com.HistoryService.dto.response.BookingHistoryRespone;
import com.HistoryService.entity.BookingHistory;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IHistoryService {
    List<BookingHistoryRespone>
    getBookingHistorys(Long bookingId, Long profileId,
                                                   Long hotelId, Long roomId,
                                                   LocalDate checkInDateStart,
                                                   LocalDate checkOutDateEnd, Pageable pageable);
    BookingHistoryRespone getBookingHistory(Long bookingHistoryId);

    String deleteBookingHistory(List<Long> bookingHistoryId);
}
