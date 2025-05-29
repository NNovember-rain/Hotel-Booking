package com.HotelService.controller.user;

import com.HotelService.dto.response.ApiResponse;
import com.HotelService.dto.response.HotelResponse;
import com.HotelService.service.HotelService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/hotel/user")
public class HotelUserController {
    HotelService hotelService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<?> getHotels(@RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "size", defaultValue = "10") int size,
                                       @RequestParam(value = "name", required = false) String name,
                                       @RequestParam(value = "address", required = false) String address) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authentication: {}", authentication);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        List<HotelResponse> hotelResponses =hotelService.getHotelsUserPage(pageable, name, address);
        ApiResponse<?> response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(hotelResponses)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{hotelId}")
    public ResponseEntity<?> getHotelById(@PathVariable Long hotelId) {
        HotelResponse hotelResponse = hotelService.getHotel(hotelId);
        ApiResponse<?> response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(hotelResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
