package uk.specialgraphics.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.specialgraphics.api.payload.request.GeneralUserProfileRequest;
import uk.specialgraphics.api.payload.request.ProfileUpdateRequest;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.service.ProfileService;
import uk.specialgraphics.api.service.RegisterService;

@RestController
@RequestMapping("/profile")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProfileController {
    @Autowired
    private ProfileService profileService;
    @PutMapping("/updateProfile")
    public SuccessResponse updateProfile(ProfileUpdateRequest profileUpdateRequest) {
        return profileService.updateProfile(profileUpdateRequest);
    }
}
