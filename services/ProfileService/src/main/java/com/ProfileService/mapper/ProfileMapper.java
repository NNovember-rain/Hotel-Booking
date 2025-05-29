package com.ProfileService.mapper;

import org.mapstruct.Mapper;

import com.ProfileService.dto.request.RegistrationRequest;
import com.ProfileService.dto.response.ProfileResponse;
import com.ProfileService.entity.Profile;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    Profile toProfile(RegistrationRequest request);

    ProfileResponse toProfileResponse(Profile profile);
}
