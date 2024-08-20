package uk.specialgraphics.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.specialgraphics.api.entity.GeneralUserProfile;
import uk.specialgraphics.api.repository.GeneralUserProfileRepository;
import uk.specialgraphics.api.service.UserProfileService;
@Service
public class UserProfileServiceImpl implements UserProfileService {
    @Autowired
    private GeneralUserProfileRepository generalUserProfileRepository;

    public GeneralUserProfile getProfile(String username) {
        return generalUserProfileRepository.getGeneralUserProfileByEmail(username);
    }
}
