package com.BookingService.service.impl;

import com.BookingService.client.HotelService;
import com.BookingService.client.ProfileService;
import com.BookingService.dto.request.BookingRequest;
import com.BookingService.dto.response.*;
import com.BookingService.entity.Booking;
import com.BookingService.enums.BookingStatus;
import com.BookingService.events.BookingHistorySavedEvent;
import com.BookingService.events.NotificationEvent;
import com.BookingService.events.RoomStatusEvent;
import com.BookingService.repository.IBookingRepository;
import com.BookingService.repository.specification.BookingSpecification;
import com.BookingService.service.IBookingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingServiceImpl implements IBookingService {

    KafkaTemplate<String, Object> kafkaTemplate;
    SimpMessagingTemplate simpMessagingTemplate;
    ObjectMapper objectMapper;
    IBookingRepository bookingRepository;
    ProfileService profileService;
    HotelService hotelService;

    @Override
    @Transactional
    public Booking startBooking(BookingRequest request) {
        try {
            Booking booking = new Booking();
            booking.setUserId(request.getUserId());
            booking.setRoomId(request.getRoomId());
            booking.setProfileId(request.getProfileId());
            booking.setHotelId(request.getHotelId());
            booking.setCheckInDate(request.getCheckInDate());
            booking.setCheckOutDate(request.getCheckOutDate());
            booking.setPrice(request.getPrice());
            booking.setStatus(BookingStatus.PENDING);
            bookingRepository.save(booking);
            log.info("Booking created with ID: {}. Status: {}", booking.getId(), booking.getStatus());

            ApiResponse<ProfileResponse> responseProfile = profileService.getProfile(booking.getUserId());
            ApiResponse<HotelResponse> responseHotel = null;
            if (booking.getHotelId() != null) {
                responseHotel = hotelService.getHotel(booking.getHotelId());
                if (responseHotel.getStatus() != 200) {
                    throw new NotFoundException("Hotel not found");
                }
            }
            ApiResponse<RoomResponse> responseRoom = null;
            if (booking.getRoomId() != null) {
                responseRoom = hotelService.getRoom(booking.getHotelId(),booking.getRoomId());
                if (responseRoom.getStatus() != 200) {
                    throw new NotFoundException("Room not found");
                }
            }
            assert responseRoom != null;
            assert responseHotel != null;
            // 2. Gửi yêu cầu kiểm tra phòng đến Hotel Service qua Kafka
            RoomStatusEvent checkRequest = RoomStatusEvent.builder()
                    .idBooking(booking.getId())
                    .roomId(request.getRoomId())
                    .hotelId(request.getHotelId())
                    .userId(request.getUserId())
                    .checkInDate(request.getCheckInDate())
                    .checkOutDate(request.getCheckOutDate())
                    .price(request.getPrice())
                    .email(responseProfile.getData().getEmail())
                    .fullName(responseProfile.getData().getFirstName() + " " + responseProfile.getData().getLastName())
                    .roomNumber(responseRoom.getData().getRoomNumber())
                    .hotelName(responseHotel.getData().getName())
                    .username(responseProfile.getData().getUsername())
                    .build();
            String message = objectMapper.writeValueAsString(checkRequest);
            kafkaTemplate.send("hotel.checkRoomAvailability", message);
            log.info("Sent room availability check request for booking {}: {}", booking.getId(), message);
            return booking;
        } catch (Exception e) {
            log.error("Error initiating booking for request: {}", request, e);
            throw new RuntimeException("Failed to initiate booking", e);
        }
    }

    @KafkaListener(topics = "room.status", groupId = "booking-service-group")
    @Transactional
    public void handleRoomStatus(String message) {
        try {
            RoomStatusEvent event = objectMapper.readValue(message, RoomStatusEvent.class);
            log.info("Received RoomStatusEvent for booking {}: {}", event.getIdBooking(), event);
            Optional<Booking> bookingOptional = bookingRepository.findById(event.getIdBooking());
            Booking booking = bookingOptional.orElseThrow(() -> new NotFoundException("Booking not found"));

            if ("AVAILABLE".equals(event.getStatus())) {
                booking.setStatus(BookingStatus.CONFIRMED);
                booking.setPrice(event.getPrice());
                bookingRepository.save(booking);
                try {
                    Map<String, String> message1 = new HashMap<>();
                    message1.put("status", booking.getStatus().name());
                    simpMessagingTemplate.convertAndSend("/topic/booking", message1);
                    log.info("Socket booking: {}", booking.getStatus());
                } catch (Exception e) {
                    log.error("Lỗi khi gửi WebSocket booking: ", e);
                }
                log.info("Booking {} confirmed: Room is available.", event.getIdBooking());
                String messageSendEmail = "Your booking " + booking.getId() + " for room " + booking.getRoomId() +
                        " from " + booking.getCheckInDate() + " to " + booking.getCheckOutDate() +
                        " has been confirmed. Total price: " + booking.getPrice() + ".";

                NotificationEvent notificationEvent = NotificationEvent.builder()
                        .checkInDate(booking.getCheckInDate())
                        .checkOutDate(booking.getCheckOutDate())
                        .roomNumber(event.getRoomNumber())
                        .hotelName(event.getHotelName())
                        .userId(booking.getUserId())
                        .type("BOOKING_CONFIRMED")
                        .message(messageSendEmail)
                        .email(event.getEmail())
                        .fullName(event.getFullName())
                        .price(event.getPrice())
                        .build();
                kafkaTemplate.send("notification.send", objectMapper.writeValueAsString(notificationEvent));
                log.info("Sent success notification for booking {}.", event.getIdBooking());
                sendBookingEventToHistory(booking.getId(), booking.getUserId(), booking.getStatus().name(), event.getHotelName(), event.getRoomNumber(), booking.getPrice());
            } else if ("BOOKED".equals(event.getStatus())) {
                booking.setStatus(BookingStatus.CANCELLED);
                bookingRepository.save(booking);
                try {
                    Map<String, String> message1 = new HashMap<>();
                    message1.put("status", booking.getStatus().name());
                    log.info(event.getUsername());
                    simpMessagingTemplate.convertAndSend("/topic/booking", message1);
                    log.info("Socket booking: {}", booking.getStatus());
                } catch (Exception e) {
                    log.error("Lỗi khi gửi WebSocket booking: ", e);
                }
                log.warn("Booking {} failed: Room is unavailable.", event.getIdBooking());
            } else {
                log.warn("Unknown room status received for booking {}: {}", event.getIdBooking(), event.getStatus());
            }


        } catch (JsonProcessingException e) {
            log.error("Error deserializing message from room.status.response: {}", message, e);
        } catch (Exception e) {
            log.error("Error processing room status event for message: {}. Details: {}", message, e.getMessage(), e);
        }
    }

    private void sendBookingEventToHistory(Long idBooking, String userId, String eventType, String hotelName, String roomNumber, double price) {
        try {
            BookingHistorySavedEvent historyEvent = BookingHistorySavedEvent.builder()
                    .bookingId(idBooking)
                    .userId(userId)
                    .status(eventType)
                    .hotelName(hotelName)
                    .roomNumber(roomNumber)
                    .price(price)
                    .build();
            kafkaTemplate.send("booking.events.history", objectMapper.writeValueAsString(historyEvent));
            log.debug("Sent booking event to history topic for booking {}: {}", idBooking, eventType);
        } catch (JsonProcessingException e) {
            log.error("Error serializing booking history event for booking {}: {}", idBooking, e.getMessage());
        }
    }

    @Override
    public List<BookingResponseDetail> searchBookings(String userId, Long hotelId, Long roomId, LocalDate checkInDateStart, LocalDate checkOutDateEnd, BookingStatus status, Double minPrice, Double maxPrice, Pageable pageable) {
        Specification<Booking> spec = BookingSpecification.filter(
                userId, hotelId, roomId, checkInDateStart, checkOutDateEnd,
                status, minPrice, maxPrice
        );
        Page<Booking> bookingPage = bookingRepository.findAll(spec, pageable);
        List<Booking> bookings = bookingPage.getContent();
        List<BookingResponseDetail> bookingResponses = new ArrayList<>();
        for (Booking booking : bookings) {
            BookingResponseDetail bookingResponse=new BookingResponseDetail();
            BeanUtils.copyProperties(booking, bookingResponse);
            ApiResponse<ProfileResponse> responseProfile = profileService.getProfile(booking.getUserId());
            ApiResponse<HotelResponse> responseHotel = null;
            if (booking.getHotelId() != null) {
                responseHotel = hotelService.getHotelAdmin(booking.getHotelId());
                if (responseHotel.getStatus() != 200) {
                    log.info("Error fetching hotel: {}", responseHotel.getMessage());
                    throw new NotFoundException("Hotel not found");
                }
            }
            ApiResponse<RoomResponse> responseRoom = null;
            if (booking.getRoomId() != null) {
                responseRoom = hotelService.getRoomAdmin(booking.getRoomId());
                if (responseRoom.getStatus() != 200) {
                    log.info("Error fetching room: {}", responseRoom.getMessage());
                    throw new NotFoundException("Room not found");
                }
            }
            bookingResponse.setFullName(responseProfile.getData().getFirstName() + " " + responseProfile.getData().getLastName());
            assert responseHotel != null;
            bookingResponse.setHotelName(responseHotel.getData().getName());
            assert responseRoom != null;
            bookingResponse.setRoomNumber(responseRoom.getData().getRoomNumber());
            bookingResponse.setRoomType(String.valueOf(responseRoom.getData().getType()));
            bookingResponse.setImageHotel(responseHotel.getData().getImage());
            bookingResponses.add(bookingResponse);
        }
        return bookingResponses;
    }

    @Override
    public BookingResponseDetail getBooking(Long bookingId) {
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(()->new NotFoundException("Booking not found"));
        BookingResponseDetail bookingResponse=new BookingResponseDetail();
        BeanUtils.copyProperties(booking, bookingResponse);
        return bookingResponse;
    }

    @Override
    public String deleteBooking(List<Long> bookingId) {
        List<Booking> bookings = bookingRepository.findAllById(bookingId);
        if(!bookings.isEmpty() && bookings.size()==bookingId.size()){
            bookingRepository.deleteAll(bookings);
            return "Booking deleted successfully";
        }else throw new NotFoundException("Some Booking not found");
    }

}