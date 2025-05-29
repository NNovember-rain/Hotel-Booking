package com.HistoryService.service.impl;

import com.HistoryService.dto.response.BookingHistoryRespone;
import com.HistoryService.entity.BookingHistory;
import com.HistoryService.events.BookingHistorySavedEvent;
import com.HistoryService.events.SaveHistoryEvent;
import com.HistoryService.repository.HistoryRepository;
import com.HistoryService.repository.specification.BookingHistorySpecification;
import com.HistoryService.service.IHistoryService;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HistoryServiceImpl implements IHistoryService {

    HistoryRepository historyRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    ObjectMapper objectMapper;

    @KafkaListener(topics = "booking.events.history", groupId = "history-service-group")
    public void handleBookingHistoryEvent(String message) {
        try {
            BookingHistorySavedEvent event = objectMapper.readValue(message, BookingHistorySavedEvent.class);
            log.info("Received booking history event: bookingId={}, userId={}, status={}",
                    event.getBookingId(), event.getUserId(), event.getStatus());
            BookingHistory bookingHistory = new BookingHistory();
            bookingHistory.setBookingId(event.getBookingId());
            bookingHistory.setUserId(event.getUserId());
            bookingHistory.setStatus(event.getStatus());
            bookingHistory.setProfileId(event.getProfileId());
            bookingHistory.setPrice(event.getPrice());
            bookingHistory.setRoomNumber(event.getRoomNumber());
            bookingHistory.setHotelName(event.getHotelName());
            historyRepository.save(bookingHistory);

        } catch (JsonProcessingException e) {
            log.error("Failed to parse booking history message: {}", message, e);
        } catch (Exception e) {
            log.error("Unexpected error processing booking history message: {}", message, e);
        }
    }

    @Override
    public List<BookingHistoryRespone> getBookingHistorys(Long bookingId, Long profileId, Long hotelId, Long roomId, LocalDate checkInDateStart, LocalDate checkOutDateEnd, Pageable pageable) {
        Specification<BookingHistory> spec= BookingHistorySpecification.filter(bookingId, profileId, hotelId, roomId, checkInDateStart, checkOutDateEnd);
        Page<BookingHistory> bookingHistoriePage = historyRepository.findAll(spec,pageable);
        List<BookingHistory> bookingHistories= bookingHistoriePage.getContent();
        List<BookingHistoryRespone> bookingHistoryRespones=new ArrayList<>();
        for (BookingHistory bookingHistory : bookingHistories) {
            BookingHistoryRespone bookingHistoryRespone=new BookingHistoryRespone();
            BeanUtils.copyProperties(bookingHistory, bookingHistoryRespone);
            bookingHistoryRespones.add(bookingHistoryRespone);
        }
        return bookingHistoryRespones;
    }

    @Override
    public BookingHistoryRespone getBookingHistory(Long bookingHistoryId) {
        BookingHistory bookingHistory=historyRepository.findById(bookingHistoryId).orElseThrow(()-> new NotFoundException("Booking history not found"));
        BookingHistoryRespone bookingHistoryRespone=new BookingHistoryRespone();
        BeanUtils.copyProperties(bookingHistory, bookingHistoryRespone);
        return bookingHistoryRespone;
    }

    @Override
    public String deleteBookingHistory(List<Long> ids) {
        List<BookingHistory> bookingHistories=historyRepository.findAllById(ids);
        if(!bookingHistories.isEmpty() && bookingHistories.size()==ids.size()) {
            historyRepository.deleteAllById(ids);
            return "Booking history deleted";
        }else throw new NotFoundException("Somne Booking history not found");
    }
}
