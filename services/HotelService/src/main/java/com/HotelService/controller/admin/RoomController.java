package com.HotelService.controller.admin;

import com.HotelService.dto.request.RoomRequest;
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
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/hotel/room")
public class RoomController {

    RoomService roomService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createRoom(@ModelAttribute RoomRequest roomRequest) {
        Long idRoom= roomService.createRoom(roomRequest);
        ApiResponse<?> response = ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message(HttpStatus.CREATED.getReasonPhrase())
                .data(idRoom)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateRoom(@PathVariable Long id, @RequestBody RoomRequest roomRequest) {
        String message=roomService.updateRoom(id, roomRequest);
        ApiResponse<?> response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(message)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{hotelId}/room")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getRooms(@RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "10") int size,
                                                    @RequestParam(value = "roomNumber", required = false) String roomNumber,
                                                    @RequestParam(value = "type", required = false) RoomType type,
                                                    @RequestParam(value = "priceFrom", required = false) Double priceFrom,
                                                    @RequestParam(value = "priceTo", required = false) Double priceTo,
                                                    @RequestParam(value = "status", required = false) RoomStatus status,
                                                    @PathVariable(value = "hotelId", required = false) Long hotelId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        List<RoomResponse> roomResponses=roomService.getRooms(pageable,roomNumber,type,priceFrom,priceTo,status,hotelId);
        ApiResponse<?> response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(roomResponses)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getRoom(@PathVariable Long id){
        RoomResponse roomResponse=roomService.getRoom(id);
        ApiResponse<?> response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(roomResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{ids}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteRoom(@PathVariable List<Long> ids) {
        String message=roomService.deleteRooms(ids);
        ApiResponse<?> response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(message)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
