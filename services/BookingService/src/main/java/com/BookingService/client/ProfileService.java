package com.BookingService.client;

import com.BookingService.config.FeignConfig;
import com.BookingService.dto.response.ApiResponse;
import com.BookingService.dto.response.ProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "profile-service", configuration = FeignConfig.class)
public interface ProfileService {
    @GetMapping("/profile/{userId}")
    ApiResponse<ProfileResponse> getProfile(@PathVariable String userId);
}
