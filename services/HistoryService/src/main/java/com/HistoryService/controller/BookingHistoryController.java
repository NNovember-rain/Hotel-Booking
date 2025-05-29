package com.HistoryService.controller;

import com.HistoryService.dto.response.ApiResponse;
import com.HistoryService.dto.response.BookingHistoryRespone;
import com.HistoryService.entity.BookingHistory;
import com.HistoryService.service.IHistoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/history")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BookingHistoryController {

    IHistoryService historyService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<?> getBookingHistorys(@RequestParam(value = "page", defaultValue = "0") int page,
                                                @RequestParam(value = "size", defaultValue = "10") int size,
                                                @RequestParam(required = false) Long bookingId,
                                                @RequestParam(required = false) Long profileId,
                                                @RequestParam(required = false) Long hotelId,
                                                @RequestParam(required = false) Long roomId,
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDateStart,
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDateEnd) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        List<BookingHistoryRespone> bookingHistories = historyService.getBookingHistorys(bookingId, profileId, hotelId, roomId, checkInDateStart, checkOutDateEnd, pageable);
        ApiResponse<?> response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(bookingHistories)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping({"/{id}"})
    public ResponseEntity<?> getBookingHistory(@PathVariable Long id) {
        BookingHistoryRespone bookingHistoryRespone=historyService.getBookingHistory(id);
        ApiResponse<?> response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(bookingHistoryRespone)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping({"/{ids}"})
    public ResponseEntity<?> deletBookingHistory(@PathVariable List<Long> ids) {
        String message=historyService.deleteBookingHistory(ids);
        ApiResponse<?> response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(message)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
