package com.HotelService.controller.user;

import com.HotelService.dto.response.ApiResponse;
import com.HotelService.dto.response.RoomResponse;
import com.HotelService.enums.RoomStatus;
import com.HotelService.enums.RoomType;
import com.HotelService.service.RoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/hotel/room/user")
public class RoomUserController {
    RoomService roomService;
    @GetMapping("/{hotelId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getRooms(@RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "size", defaultValue = "10") int size,
                                      @RequestParam(value = "type", required = false) RoomType type,
                                      @RequestParam(value = "priceFrom", required = false) Double priceFrom,
                                      @RequestParam(value = "priceTo", required = false) Double priceTo,
                                      @RequestParam(value = "status", required = false) RoomStatus status,
                                      @PathVariable Long hotelId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        List<RoomResponse> roomResponses=roomService.getRoomsUserPage(pageable,type,priceFrom,priceTo,status,hotelId);
        ApiResponse<?> response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(roomResponses)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{hotelId}/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getRoom(@PathVariable Long hotelId,
                                     @PathVariable Long id){
        RoomResponse roomResponse=roomService.getRoom(id);
        ApiResponse<?> response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(roomResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
