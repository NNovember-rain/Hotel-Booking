package com.ProfileService.controller;

import java.util.List;

import com.ProfileService.dto.identity.UserCreationParam;
import com.ProfileService.dto.response.RoleResponse;
import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ProfileService.dto.response.ApiResponse;
import com.ProfileService.dto.request.RegistrationRequest;
import com.ProfileService.dto.response.ProfileResponse;
import com.ProfileService.service.ProfileService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProfileController {
    ProfileService profileService;


    @PostMapping("/register")
    ApiResponse<ProfileResponse> register(@RequestBody @Valid RegistrationRequest request) {
        return ApiResponse.<ProfileResponse>builder()
                .data(profileService.register(request))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    ApiResponse<List<UserCreationParam>> getAllUser() {
        log.info("Get user");
        return ApiResponse.<List<UserCreationParam>>builder()
                .data(profileService.getUsers())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/roles")
    ApiResponse<List<RoleResponse>> getRoles() {
        return ApiResponse.<List<RoleResponse>>builder()
                .data(profileService.getRoles())
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserCreationParam> getProfile(@PathVariable String userId) {
        return ApiResponse.<UserCreationParam>builder()
                .data(profileService.getUser(userId))
                .build();
    }

}
