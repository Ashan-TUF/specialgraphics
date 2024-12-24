package uk.specialgraphics.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.specialgraphics.api.entity.GeneralUserProfile;
import uk.specialgraphics.api.payload.request.ChangePasswordRequest;
import uk.specialgraphics.api.payload.request.UploadZipRequest;
import uk.specialgraphics.api.payload.request.UserProfileUpdateRequest;
import uk.specialgraphics.api.payload.request.UserUpdateProfileRequest;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.payload.response.UserLoginResponse;
import uk.specialgraphics.api.payload.response.UserProfileResponse;
import uk.specialgraphics.api.repository.GeneralUserProfileRepository;
import uk.specialgraphics.api.service.AssignmentService;
import uk.specialgraphics.api.service.UserProfileService;

import java.util.List;

@RestController
@RequestMapping("/students")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class StudentController {


    @Autowired
    UserProfileService userProfileService;

//    @GetMapping("/getAllStudents")
//    public List<UserProfileResponse> getAllStudents() {
//      return  userProfileService.getAllStudentProfiles();
//    }


    @GetMapping("/getAllStudents")
    public ResponseEntity<Page<GeneralUserProfile>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<GeneralUserProfile> students = userProfileService.getAllStudents(page, size);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @GetMapping("/getStudentDetails")
    public UserProfileResponse getStudentProfile() {
        return userProfileService.getStudentProfiles();
    }


    @GetMapping("/getStudentProfileDetails/{email}")
    public UserProfileResponse getStudentProfileForUpdate(@PathVariable String email) {
        return userProfileService.getUserProfileByEmail(email);
    }

    @GetMapping("/studentProfileDetails")
    public UserProfileResponse studentProfileForUpdate() {
        return userProfileService.studentGetUserProfileByEmail();
    }


    @GetMapping("/changeStudentStatus/{email}")
    public SuccessResponse changeStudentStatus(@PathVariable String email) {
        return userProfileService.updateUserProfileStatus(email);
    }

    @PutMapping("/updateUserProfileDetails")
    public SuccessResponse changeStudentStatus(UserProfileUpdateRequest userProfileUpdateRequest) {
        return userProfileService.updateUserByEmail(userProfileUpdateRequest);
    }

    @PutMapping("/userUpdateProfile")
    public UserLoginResponse studentChangeProfile(UserUpdateProfileRequest userProfileUpdateRequest) {
        return userProfileService.updateUserByEmail(userProfileUpdateRequest);
    }

    @PutMapping("/userUpdatePassword")
    public UserLoginResponse studentChangePassword(ChangePasswordRequest changePasswordRequest) {
        return userProfileService.changeTheStudentPassword(changePasswordRequest);
    }



}
