package com.HotelService.service;

import com.HotelService.dto.request.RoomRequest;
import com.HotelService.dto.response.RoomResponse;
import com.HotelService.enums.RoomStatus;
import com.HotelService.enums.RoomType;
import org.aspectj.bridge.Message;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoomService {
    List<RoomResponse> getRooms(Pageable pageable, String roomNumber, RoomType type,
                                Double priceFrom, Double priceTo, RoomStatus status, Long hotelId);
    RoomResponse getRoom(Long id);
    Long createRoom(RoomRequest roomRequest);
    String updateRoom(Long id, RoomRequest roomRequest);
    String deleteRooms(List<Long> ids);
    boolean checkRoomAvailable(Long roomId);
    List<RoomResponse> getRoomsUserPage(Pageable pageable, RoomType type,
                                Double priceFrom, Double priceTo, RoomStatus status, Long hotelId);
}
