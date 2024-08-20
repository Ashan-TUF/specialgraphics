package uk.specialgraphics.api.service;

import uk.specialgraphics.api.payload.request.GeneralUserProfileRequest;
import uk.specialgraphics.api.payload.response.GeneralUserProfileResponse;

public interface RegisterService {

    GeneralUserProfileResponse saveUser(GeneralUserProfileRequest generalUserProfileRequest);
}
