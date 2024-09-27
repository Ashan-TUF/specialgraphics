package uk.specialgraphics.api.service;

import uk.specialgraphics.api.payload.request.ProfileUpdateRequest;
import uk.specialgraphics.api.payload.response.SuccessResponse;

public interface ProfileService {
    SuccessResponse updateProfile(ProfileUpdateRequest profileUpdateRequest);
}
