package com.ProfileService.service;

import java.util.List;
import java.util.stream.Collectors;

import com.ProfileService.dto.response.RoleResponse;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ProfileService.client.IdentityClient;
import com.ProfileService.dto.identity.Credential;
import com.ProfileService.dto.identity.TokenExchangeParam;
import com.ProfileService.dto.identity.TokenExchangeResponse;
import com.ProfileService.dto.identity.UserCreationParam;
import com.ProfileService.dto.request.RegistrationRequest;
import com.ProfileService.dto.response.ProfileResponse;
import com.ProfileService.entity.Profile;
import com.ProfileService.exception.AppException;
import com.ProfileService.exception.ErrorCode;
import com.ProfileService.exception.ErrorNormalizer;
import com.ProfileService.mapper.ProfileMapper;
import com.ProfileService.repository.ProfileRepository;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileService {
    ProfileRepository profileRepository;
    ProfileMapper profileMapper;
    IdentityClient identityClient;
    ErrorNormalizer errorNormalizer;

    @Value("${keycloak.client.id}")
    @NonFinal
    String clientId;

    @Value("${keycloak.client.secret}")
    @NonFinal
    String clientSecret;

    public List<ProfileResponse> getAllProfiles() {
        var profiles = profileRepository.findAll();
        return profiles.stream().map(profileMapper::toProfileResponse).toList();
    }

    public ProfileResponse getMyProfiles() {
        // TODO: Lấy user đang login từ securityContextHolder
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        // TODO: getName la Sub token chinh la userId
        String userId = authentication.getName();
        var profiles =
                profileRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));
        return profileMapper.toProfileResponse(profiles);
    }

    public ProfileResponse register(RegistrationRequest request) {
        try {
            // Exchange client Token
            TokenExchangeResponse tokenExchangeResponse = identityClient.exchangeToken(TokenExchangeParam.builder()
                    .grant_type("client_credentials")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .scope("openid")
                    .build());
            log.info("Token exchange response: {}", tokenExchangeResponse);

            // Create user in Keycloak
            ResponseEntity<?> profileResponse = identityClient.createUser(
                    "Bearer " + tokenExchangeResponse.getAccessToken(),
                    UserCreationParam.builder()
                            .email(request.getEmail())
                            .username(request.getUsername())
                            .firstName(request.getFirstName())
                            .lastName(request.getLastName())
                            .enabled(true)
                            .emailVerified(false)
                            .credentials(List.of(Credential.builder()
                                    .type("password")
                                    .value(request.getPassword())
                                    .temporary(false)
                                    .build()))
                            .build());
            String userId = extractUserId(profileResponse);
            log.info("User created in Keycloak with ID: {}", userId);

            // Get the userId from Keycloak response
            Profile profile = profileMapper.toProfile(request);
            profile.setUserId(userId);
            profile = profileRepository.save(profile);

            return profileMapper.toProfileResponse(profile);
        } catch (FeignException.FeignClientException e) {
            throw errorNormalizer.handelKeycloakException(e);
        }
    }

    public List<UserCreationParam> getUsers() {
        TokenExchangeResponse tokenExchangeResponse = identityClient.exchangeToken(TokenExchangeParam.builder()
                .grant_type("client_credentials")
                .client_id(clientId)
                .client_secret(clientSecret)
                .scope("openid")
                .build());
        return identityClient.getUsers(
                "Bearer " + tokenExchangeResponse.getAccessToken());
    }

    public UserCreationParam getUser(String userId) {
        TokenExchangeResponse tokenExchangeResponse = identityClient.exchangeToken(TokenExchangeParam.builder()
                .grant_type("client_credentials")
                .client_id(clientId)
                .client_secret(clientSecret)
                .scope("openid")
                .build());
        return identityClient.getUser("Bearer " + tokenExchangeResponse.getAccessToken(), userId);
    }

    public List<RoleResponse> getRoles() {
        TokenExchangeResponse tokenExchangeResponse = identityClient.exchangeToken(TokenExchangeParam.builder()
                .grant_type("client_credentials")
                .client_id(clientId)
                .client_secret(clientSecret)
                .scope("openid")
                .build());

        return identityClient.getRoles("Bearer " + tokenExchangeResponse.getAccessToken()).stream()
                .filter(role -> role.getDescription() != null &&
                        !role.getDescription().contains("${role_") &&
                        !role.getDescription().equalsIgnoreCase("offline_access") &&
                        !role.getDescription().equalsIgnoreCase("uma_authorization") &&
                        !role.getDescription().equalsIgnoreCase("default-roles-" + clientId))
                .toList();
    }

    private String extractUserId(ResponseEntity<?> response) {
        String location = response.getHeaders().get("Location").get(0);
        String[] parts = location.split("/");
        return parts[parts.length - 1];
    }

    public ProfileResponse getMyProfile(Long id) {
        Profile profile = profileRepository.findById(id).orElseThrow(() -> new NotFoundException("Profile User not found"));
        return profileMapper.toProfileResponse(profile);
    }
}
