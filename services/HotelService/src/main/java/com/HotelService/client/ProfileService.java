package com.HotelService.client;

import com.HotelService.dto.response.ProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "Profile-Service")
public interface ProfileService {
    @GetMapping("/profile/{id}")
    ProfileResponse getProfile(@PathVariable Long id);
}
