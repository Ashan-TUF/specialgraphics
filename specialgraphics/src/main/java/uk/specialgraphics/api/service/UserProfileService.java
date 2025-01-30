package uk.specialgraphics.api.service;

import org.springframework.data.domain.Page;
import uk.specialgraphics.api.entity.GeneralUserProfile;
import uk.specialgraphics.api.payload.request.ChangePasswordRequest;
import uk.specialgraphics.api.payload.request.UserProfileUpdateRequest;
import uk.specialgraphics.api.payload.request.UserUpdateProfileRequest;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.payload.response.UserLoginResponse;
import uk.specialgraphics.api.payload.response.UserProfileResponse;

import java.util.List;

public interface UserProfileService {
    GeneralUserProfile getProfile(String username);
//    List<UserProfileResponse> getAllStudentProfiles();

    Page<GeneralUserProfile> getAllStudents(int page, int size);
    UserProfileResponse getStudentProfiles();

    UserProfileResponse getUserProfileByEmail(String email);

    SuccessResponse updateUserProfileStatus(String email);

    SuccessResponse updateUserByEmail(UserProfileUpdateRequest userProfileUpdateRequest);

    UserLoginResponse updateUserByEmail(UserUpdateProfileRequest userUpdateProfileRequest);

    UserProfileResponse studentGetUserProfileByEmail();

    UserLoginResponse changeTheStudentPassword(ChangePasswordRequest changePasswordRequest);

    UserProfileResponse adminGetUserProfileByEmail();

    UserLoginResponse adminUpdateProfile(UserUpdateProfileRequest userUpdateProfileRequest);

    UserLoginResponse changeAdminPassword(ChangePasswordRequest changePasswordRequest);


}
