package com.BookingService.client;

import com.BookingService.config.FeignConfig;
import com.BookingService.dto.response.ApiResponse;
import com.BookingService.dto.response.HotelResponse;
import com.BookingService.dto.response.RoomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hotel-service", configuration = FeignConfig.class)
public interface HotelService {
    @GetMapping("/hotel/{id}")
    ApiResponse<HotelResponse> getHotelAdmin(@PathVariable Long id);

    @GetMapping("/hotel/room/{id}")
    ApiResponse<RoomResponse> getRoomAdmin(@PathVariable Long id);

    @GetMapping("/hotel/user/{id}")
    ApiResponse<HotelResponse> getHotel(@PathVariable Long id);

    @GetMapping("/hotel/room/user/{hotelId}/{id}")
    ApiResponse<RoomResponse> getRoom(@PathVariable Long hotelId,
                                      @PathVariable Long id);
}
