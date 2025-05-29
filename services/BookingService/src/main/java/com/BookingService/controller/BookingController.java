package com.BookingService.controller;

import com.BookingService.dto.request.BookingRequest;
import com.BookingService.dto.response.ApiResponse;
import com.BookingService.dto.response.BookingResponse;
import com.BookingService.dto.response.BookingResponseDetail;
import com.BookingService.enums.BookingStatus;
import com.BookingService.service.IBookingService;
import com.BookingService.service.impl.BookingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {
    private final IBookingService bookingService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    public ResponseEntity<String> createBooking(@RequestBody BookingRequest request) {
        bookingService.startBooking(request);
        return ResponseEntity.ok("Booking process started. You will be notified of the result.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getBookings(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Long hotelId,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDateStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDateEnd,
            @RequestParam(required = false) BookingStatus status, // Spring can usually convert string param to Enum
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        List<BookingResponseDetail> bookingResponses = bookingService.searchBookings(userId, hotelId, roomId, checkInDateStart,
                                                                            checkOutDateEnd, status, minPrice, maxPrice, pageable);
        ApiResponse<?> response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(bookingResponses)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getBooking(@PathVariable Long id) {
        BookingResponseDetail bookingResponses = bookingService.getBooking(id);
        ApiResponse<?> response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(bookingResponses)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{ids}")
    public ResponseEntity<?> deleteBooking(@PathVariable List<Long> ids) {
        String message = bookingService.deleteBooking(ids);
        ApiResponse<?> response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(message)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
