package com.ProfileService.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.ProfileService.dto.response.KeycloakError;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ErrorNormalizer {
    // TODO: serzalize String to object
    private final ObjectMapper objectMapper;
    private final Map<String, ErrorCode> errorMap;

    public ErrorNormalizer() {
        objectMapper = new ObjectMapper();
        errorMap = new HashMap<>();
        errorMap.put("User exists with same email", ErrorCode.EMAIL_ALREADY_EXISTS);
        errorMap.put("User exists with same username", ErrorCode.USERNAME_ALREADY_EXISTS);
        errorMap.put("User name is missing", ErrorCode.USERNAME_IS_MISSING);
    }

    public AppException handelKeycloakException(FeignException feignException) {
        try {
            log.warn("Cannot parse keycloak error: ", feignException);
            var response = objectMapper.readValue(feignException.contentUTF8(), KeycloakError.class);
            if (Objects.nonNull(response.getErrorMessage())) {
                var errorCode = errorMap.get(response.getErrorMessage());
                if (Objects.nonNull(errorCode)) {
                    return new AppException(errorCode);
                }
            }
        } catch (Exception e) {
            log.error("Error while parsing keycloak error: ", e);
        }
        return new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
    }
}
