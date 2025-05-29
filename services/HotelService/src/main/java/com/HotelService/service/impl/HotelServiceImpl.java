package com.HotelService.service.impl;

import com.HotelService.dto.request.BookingRequest;
import com.HotelService.dto.request.HotelRequest;
import com.HotelService.dto.response.HotelResponse;
import com.HotelService.entity.Hotel;
import com.HotelService.entity.Room;
import com.HotelService.enums.RoomStatus;
import com.HotelService.events.RoomStatusEvent;
import com.HotelService.repository.HotelRepository;
import com.HotelService.repository.RoomRepository;
import com.HotelService.repository.specification.HotelSpecification;
import com.HotelService.service.HotelService;
import com.HotelService.service.RoomService;
import com.HotelService.utils.UploadImageUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.NotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class HotelServiceImpl implements HotelService {

    HotelRepository hotelRepository;
    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;
    RoomService roomService;
    ObjectMapper objectMapper;
    RoomRepository roomRepository;
    UploadImageUtil uploadImageUtil;

    @KafkaListener(topics = "hotel.checkRoomAvailability", groupId = "hotel-service-group")
    public void consumeCheckRoom(String message) {
        try {
            RoomStatusEvent request = objectMapper.readValue(message, RoomStatusEvent.class);
            boolean checkRoom = roomService.checkRoomAvailable(request.getRoomId());
            RoomStatusEvent event = RoomStatusEvent.builder()
                    .requestId(request.getRequestId())
                    .idBooking(request.getIdBooking())
                    .roomId(request.getRoomId())
                    .profileId(request.getProfileId())
                    .hotelId(request.getHotelId())
                    .userId(request.getUserId())
                    .email(request.getEmail())
                    .fullName(request.getFullName())
                    .roomNumber(request.getRoomNumber())
                    .hotelName(request.getHotelName())
                    .checkInDate(request.getCheckInDate())
                    .checkOutDate(request.getCheckOutDate())
                    .price(request.getPrice())
                    .username(request.getUsername())
                    .build();

            if (checkRoom) {
                event.setStatus(RoomStatus.AVAILABLE.name());
                Room room = roomRepository.findById(request.getRoomId()).orElseThrow();
                room.setStatus(RoomStatus.BOOKED);
                roomRepository.save(room);
            } else {
                event.setStatus(RoomStatus.BOOKED.name());
            }

            String data = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("room.status", data);
            log.info("Booking request consumed and sent to room.status topic: {}", request);

        } catch (JsonProcessingException e) {
            log.error("Error deserializing message: {}", message, e);
        }
    }

    @Override
    public Long createHotel(HotelRequest hotelRequest) {
        Hotel hotel = new Hotel();
        BeanUtils.copyProperties(hotelRequest, hotel);
        if(!hotelRequest.getImage().isEmpty()) {
            try {
                hotel.setImage(uploadImageUtil.upload(hotelRequest.getImage()));
            } catch (Exception e) {
                log.error("Could not upload the image for hotel");
                throw new RuntimeException("Could not upload the image, please try again later !");
            }
        }
        Hotel hotelSaved=hotelRepository.save(hotel);
        return hotelSaved.getId();
    }

    @Override
    public String updateHotel(Long id, HotelRequest hotelRequest) {
        Optional<Hotel> hotelOptional=hotelRepository.findById(id);
        if (hotelOptional.isPresent()) {
            Hotel hotel=hotelOptional.get();
            BeanUtils.copyProperties(hotelRequest, hotel,"id");
            hotelRepository.save(hotel);
            return "Hotel updated";
        }else throw new NotFoundException("Hotel not found");
    }

    @Override
    public HotelResponse getHotel(Long id) {
        Optional<Hotel> hotelOptional=hotelRepository.findById(id);
        if (hotelOptional.isPresent()) {
            Hotel hotel=hotelOptional.get();
            HotelResponse hotelResponse=new HotelResponse();
            BeanUtils.copyProperties(hotel, hotelResponse);
            hotelResponse.setImage(hotel.getImage());
            return hotelResponse;
        }else throw new NotFoundException("Hotel not found");
    }

    @Override
    public String deleteHotel(List<Long> ids) {
        List<Hotel> hotels= hotelRepository.findAllById(ids);
        if(!hotels.isEmpty() && hotels.size() == ids.size() ) {
            hotelRepository.deleteAll(hotels);
            return "Deleted Hotel";
        }else throw new NotFoundException("Hotel not found");
    }

    @Override
    public List<HotelResponse> getHotels(Pageable pageable, String name, String address, String phone, String email) {
        Specification<Hotel> spec = HotelSpecification.filter(name, address, phone, email);
        Page<Hotel> hotelPage = hotelRepository.findAll(spec, pageable);
        List<Hotel> hotels= hotelPage.getContent();
        List<HotelResponse> hotelResponses=new ArrayList<>();
        for(Hotel hotel:hotels) {
            HotelResponse hotelResponse=new HotelResponse();
            BeanUtils.copyProperties(hotel, hotelResponse);
            hotelResponses.add(hotelResponse);
        }
        return hotelResponses;
    }

    @Override
    public List<HotelResponse> getHotelsUserPage(Pageable pageable, String name, String address) {
        List<Hotel> hotels= hotelRepository.findByNameAndAddress(name,address,pageable).getContent();
        List<HotelResponse> hotelResponses=new ArrayList<>();
        for(Hotel hotel:hotels) {
            HotelResponse hotelResponse=new HotelResponse();
            BeanUtils.copyProperties(hotel, hotelResponse);
            hotelResponse.setImage(hotel.getImage());
            hotelResponses.add(hotelResponse);
        }
        return hotelResponses;
    }
}
