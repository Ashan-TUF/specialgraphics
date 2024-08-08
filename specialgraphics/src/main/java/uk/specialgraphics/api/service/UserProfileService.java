package uk.specialgraphics.api.service;

import uk.specialgraphics.api.entity.GeneralUserProfile;

public interface UserProfileService {
    GeneralUserProfile getProfile(String username);
}
