package uk.specialgraphics.api.service;

import uk.specialgraphics.api.entity.GeneralUserProfile;
import uk.specialgraphics.api.payload.request.ProfileUpdateRequest;
import uk.specialgraphics.api.payload.response.SuccessResponse;

import java.util.List;

public interface ProfileService {
    SuccessResponse updateProfile(ProfileUpdateRequest profileUpdateRequest);

}
