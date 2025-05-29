package com.ProfileService.client;

import com.ProfileService.dto.response.RoleResponse;
import com.ProfileService.entity.Profile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ProfileService.dto.identity.TokenExchangeParam;
import com.ProfileService.dto.identity.TokenExchangeResponse;
import com.ProfileService.dto.identity.UserCreationParam;

import feign.QueryMap;

import java.util.List;

@FeignClient(name = "identity-service", url = "${identity-service.url}")
public interface IdentityClient {
    @PostMapping(
            value = "/realms/hotel/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenExchangeResponse exchangeToken(@QueryMap TokenExchangeParam tokenExchangeParam);

    @PostMapping(value = "/admin/realms/hotel/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    // TODO: lấy t.tin từ header id user
    ResponseEntity<?> createUser(
            @RequestHeader("Authorization") String authorization, @RequestBody UserCreationParam userCreationParam);

    @GetMapping("/admin/realms/hotel/users")
    List<UserCreationParam> getUsers(
            @RequestHeader("Authorization") String token
    );

    @GetMapping("/admin/realms/hotel/roles")
    List<RoleResponse> getRoles(
            @RequestHeader("Authorization") String token
    );

    @GetMapping("/admin/realms/hotel/users/{user-id}")
    UserCreationParam getUser(
            @RequestHeader("Authorization") String token, @PathVariable("user-id") String userId
    );
}
