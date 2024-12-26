package uk.specialgraphics.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.specialgraphics.api.payload.request.UserUpdateProfileRequest;
import uk.specialgraphics.api.payload.response.UserLoginResponse;
import uk.specialgraphics.api.payload.response.UserProfileResponse;
import uk.specialgraphics.api.service.UserProfileService;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class AdminController {


    @Autowired
    UserProfileService userProfileService;

    @GetMapping("/getAllDashBoardDetails")
    public void getAllDashBoardDetails() {

    }

    @GetMapping("/adminProfileDetails")
    public UserProfileResponse studentProfileForUpdate() {
        return userProfileService.adminGetUserProfileByEmail();
    }

    @PutMapping("/adminUpdateProfile")
    public UserLoginResponse studentChangeProfile(UserUpdateProfileRequest userProfileUpdateRequest) {
        return userProfileService.adminUpdateProfile(userProfileUpdateRequest);
    }
}
