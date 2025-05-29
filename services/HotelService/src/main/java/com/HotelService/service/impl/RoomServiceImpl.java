package com.HotelService.service.impl;

import com.HotelService.dto.request.RoomRequest;
import com.HotelService.dto.response.HotelNameResponse;
import com.HotelService.dto.response.RoomResponse;
import com.HotelService.entity.Hotel;
import com.HotelService.entity.Room;
import com.HotelService.enums.RoomStatus;
import com.HotelService.enums.RoomType;
import com.HotelService.repository.HotelRepository;
import com.HotelService.repository.RoomRepository;
import com.HotelService.repository.specification.RoomSpecification;
import com.HotelService.service.RoomService;
import com.HotelService.utils.UploadImageUtil;
import jakarta.ws.rs.NotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomServiceImpl implements RoomService {
    RoomRepository roomRepository;
    HotelRepository hotelRepository;
    UploadImageUtil uploadImageUtil;

    @Override
    public List<RoomResponse> getRooms(Pageable pageable, String roomNumber, RoomType type, Double priceFrom, Double priceTo, RoomStatus status, Long hotelId) {
        Specification<Room> spec = RoomSpecification.filter(roomNumber, type, priceFrom, priceTo, status, hotelId);
        Page<Room> roomPage = roomRepository.findAll(spec, pageable);
        List<Room> rooms = roomPage.getContent();
        List<RoomResponse> roomResponses=new ArrayList<>();
        for (Room room : rooms) {
            RoomResponse roomResponse = new RoomResponse();
            BeanUtils.copyProperties(room, roomResponse);
            roomResponse.setType(room.getType().toString());
            roomResponse.setStatus(room.getStatus().toString());
            roomResponses.add(roomResponse);
        }
        return roomResponses;
    }

    @Override
    public RoomResponse getRoom(Long id) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new NotFoundException("Room not found"));
        RoomResponse roomResponse = new RoomResponse();
        BeanUtils.copyProperties(room, roomResponse);
        roomResponse.setType(room.getType().toString());
        roomResponse.setStatus(room.getStatus().toString());
        return roomResponse;
    }

    @Override
    public Long createRoom(RoomRequest roomRequest) {
        Hotel hotel = hotelRepository.findById(roomRequest.getHotelId()).orElseThrow(() -> new NotFoundException("Hotel not found"));
        Room room = new Room();
        BeanUtils.copyProperties(roomRequest, room);
        room.setStatus(RoomStatus.valueOf(roomRequest.getStatus()));
        room.setType(RoomType.valueOf(roomRequest.getType()));
        if(!roomRequest.getImage().isEmpty()) {
            try {
                room.setImage(uploadImageUtil.upload(roomRequest.getImage()));
            } catch (Exception e) {
                log.error("Could not upload the image for hotel");
                throw new RuntimeException("Could not upload the image, please try again later !");
            }
        }
        room.setHotel(hotel);
        Room roomSaved=roomRepository.save(room);
        return roomSaved.getId();
    }

    @Override
    public String updateRoom(Long id, RoomRequest roomRequest) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new NotFoundException("Room not found"));
        Hotel hotel = hotelRepository.findById(roomRequest.getHotelId()).orElseThrow(() -> new NotFoundException("Hotel not found"));
        BeanUtils.copyProperties(roomRequest, room,"id");
        room.setHotel(hotel);
        Room roomSaved=roomRepository.save(room);
        return "Updated Room";
    }

    @Override
    public String deleteRooms(List<Long> ids) {
        List<Room> rooms = roomRepository.findAllById(ids);
        if (!rooms.isEmpty() && rooms.size() == ids.size() ) {
            roomRepository.deleteAll(rooms);
            return "Deleted Rooms";
        }else throw new NotFoundException("Some Rooms not found");
    }

    @Override
    public boolean checkRoomAvailable(Long roomId) {
        Optional<Room> roomOptional = roomRepository.findById(roomId);
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            if (room.getStatus() == RoomStatus.AVAILABLE) {
                return true;
            } else {
                log.info("Room is not available");
            }
        }
        return false;
    }

    @Override
    public List<RoomResponse> getRoomsUserPage(Pageable pageable, RoomType type, Double priceFrom, Double priceTo, RoomStatus status,Long hotelId) {
        List<Room> rooms=roomRepository.findRoomsByHotelAndOptionalCriteria(hotelId,type, priceFrom, priceTo, status, pageable).getContent();
        List<RoomResponse> roomResponses=new ArrayList<>();
        for (Room room : rooms) {
            RoomResponse roomResponse = new RoomResponse();
            BeanUtils.copyProperties(room, roomResponse);
            roomResponse.setType(room.getType().toString());
            roomResponse.setStatus(room.getStatus().toString());
            roomResponses.add(roomResponse);
        }
        return roomResponses;
    }
}
