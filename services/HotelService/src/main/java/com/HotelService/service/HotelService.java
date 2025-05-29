package com.HotelService.service;

import com.HotelService.dto.request.HotelRequest;
import com.HotelService.dto.response.HotelResponse;
import org.springframework.data.domain.Pageable;
import java.util.List;
public interface HotelService {
    Long createHotel(HotelRequest hotelRequest);
    String updateHotel(Long id, HotelRequest hotelRequest);
    HotelResponse getHotel(Long id);
    String deleteHotel(List<Long> ids);
    List<HotelResponse> getHotels(Pageable pageable, String name, String address, String phone, String email);
    List<HotelResponse> getHotelsUserPage(Pageable pageable, String name, String address);
}