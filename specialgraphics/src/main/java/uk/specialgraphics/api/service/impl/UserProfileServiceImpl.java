package uk.specialgraphics.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.specialgraphics.api.config.PasswordEncoderConfig;
import uk.specialgraphics.api.entity.Country;
import uk.specialgraphics.api.entity.Course;
import uk.specialgraphics.api.entity.GeneralUserProfile;
import uk.specialgraphics.api.exception.ErrorException;
import uk.specialgraphics.api.payload.request.ChangePasswordRequest;
import uk.specialgraphics.api.payload.request.CourseRequest;
import uk.specialgraphics.api.payload.request.UserProfileUpdateRequest;
import uk.specialgraphics.api.payload.request.UserUpdateProfileRequest;
import uk.specialgraphics.api.payload.response.FileUploadResponse;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.payload.response.UserLoginResponse;
import uk.specialgraphics.api.payload.response.UserProfileResponse;
import uk.specialgraphics.api.repository.CountryRepository;
import uk.specialgraphics.api.repository.GeneralUserProfileRepository;
import uk.specialgraphics.api.security.JwtTokenUtil;
import uk.specialgraphics.api.security.JwtUserDetailsServicePassword;
import uk.specialgraphics.api.service.UserProfileService;
import uk.specialgraphics.api.utils.FileUploadUtil;
import uk.specialgraphics.api.utils.VarList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    @Autowired
    private GeneralUserProfileRepository generalUserProfileRepository;
    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsServicePassword userDetailsServicePassword;

    public GeneralUserProfile getProfile(String username) {
        return generalUserProfileRepository.getGeneralUserProfileByEmail(username);
    }

    private GeneralUserProfile authentication() {
        Authentication authentication;
        String username;
        GeneralUserProfile profile;
        authentication = SecurityContextHolder.getContext().getAuthentication();
        username = authentication.getName();
        profile = this.getProfile(username);

        if (profile == null) throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);

        if (profile.getIsActive() != 1) throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);

        if (profile.getGupType().getId() != 1)
            throw new ErrorException("You are not a instructor to this operation", VarList.RSP_NO_DATA_FOUND);
        return profile;
    }

    private GeneralUserProfile studentAuthentication() {
        Authentication authentication;
        String username;
        GeneralUserProfile profile;
        authentication = SecurityContextHolder.getContext().getAuthentication();
        username = authentication.getName();
        profile = this.getProfile(username);

        if (profile == null)
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);

        if (profile.getIsActive() != 1)
            throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);

        if (profile.getGupType().getId() != 2)
            throw new ErrorException("You are not a student to this operation", VarList.RSP_NO_DATA_FOUND);
        return profile;
    }

//    public List<UserProfileResponse> getAllStudentProfiles() {
//        authentication();
//        List<GeneralUserProfile> allStudentsDetails = generalUserProfileRepository.getAllStudentsDetails();
//        List<UserProfileResponse> userProfileResponses = new ArrayList<>();
//        for (GeneralUserProfile generalUserProfile : allStudentsDetails) {
//
//            UserProfileResponse userProfileResponse = new UserProfileResponse();
//            userProfileResponse.setFname(generalUserProfile.getFirstName());
//            userProfileResponse.setLname(generalUserProfile.getLastName());
//            userProfileResponse.setEmail(generalUserProfile.getEmail());
//            userProfileResponse.setMobile(generalUserProfile.getMobile());
//            userProfileResponse.setCountry(generalUserProfile.getCountry().getName());
//            userProfileResponse.setRegistred_date(generalUserProfile.getRegisteredDate().toString());
//
//            if (generalUserProfile.getIsActive() == 1) {
//                userProfileResponse.setStatus(true);
//            } else {
//                userProfileResponse.setStatus(false);
//            }
//            userProfileResponses.add(userProfileResponse);
//        }
//        return userProfileResponses;
//    }


    public Page<GeneralUserProfile> getAllStudents(int page, int size) {
        authentication();
        Pageable pageable = PageRequest.of(page, size);
        return generalUserProfileRepository.getAllStudentsDetails(pageable);
    }

    public UserProfileResponse getStudentProfiles() {

        GeneralUserProfile generalUserProfile = studentAuthentication();
        if (generalUserProfile == null) {
            throw new ErrorException("Invalid User", VarList.RSP_NO_DATA_FOUND);
        }

        UserProfileResponse userProfileResponse = new UserProfileResponse();
        userProfileResponse.setFname(generalUserProfile.getFirstName());
        userProfileResponse.setLname(generalUserProfile.getLastName());
        userProfileResponse.setEmail(generalUserProfile.getEmail());
        userProfileResponse.setMobile(generalUserProfile.getMobile());
        userProfileResponse.setCountry(generalUserProfile.getCountry().getName());
        userProfileResponse.setRegistred_date(generalUserProfile.getRegisteredDate().toString());
        userProfileResponse.setStatus(true);
        if (generalUserProfile.getIsActive() == 1) {
            userProfileResponse.setStatus(true);
        } else {
            userProfileResponse.setStatus(false);
        }


        return userProfileResponse;
    }

    public UserProfileResponse getUserProfileByEmail(String email) {
        authentication();
        if (email == null) {
            throw new ErrorException("Empty User Email", VarList.RSP_NO_DATA_FOUND);
        }
        GeneralUserProfile generalUserProfileByEmail = generalUserProfileRepository.getGeneralUserProfileByEmail(email);
        if (generalUserProfileByEmail == null) {
            throw new ErrorException("Invalid User Email", VarList.RSP_NO_DATA_FOUND);
        }

        UserProfileResponse userProfileResponse = new UserProfileResponse();
        userProfileResponse.setFname(generalUserProfileByEmail.getFirstName());
        userProfileResponse.setLname(generalUserProfileByEmail.getLastName());
        userProfileResponse.setEmail(generalUserProfileByEmail.getEmail());
        userProfileResponse.setMobile(generalUserProfileByEmail.getMobile());
        userProfileResponse.setRegistred_date(generalUserProfileByEmail.getRegisteredDate().toString());
        if (generalUserProfileByEmail.getIsActive() == 1) {
            userProfileResponse.setStatus(true);
        } else {
            userProfileResponse.setStatus(false);
        }
        userProfileResponse.setCountry_id(generalUserProfileByEmail.getCountry().getId());
        userProfileResponse.setImg(generalUserProfileByEmail.getProfileImg());

        return userProfileResponse;
    }

    @Override
    public UserProfileResponse studentGetUserProfileByEmail() {
        GeneralUserProfile generalUserProfileByEmail = studentAuthentication();
        if (generalUserProfileByEmail == null) {
            throw new ErrorException("Invalid User Email", VarList.RSP_NO_DATA_FOUND);
        }

        UserProfileResponse userProfileResponse = new UserProfileResponse();
        userProfileResponse.setFname(generalUserProfileByEmail.getFirstName());
        userProfileResponse.setLname(generalUserProfileByEmail.getLastName());
        userProfileResponse.setEmail(generalUserProfileByEmail.getEmail());
        userProfileResponse.setMobile(generalUserProfileByEmail.getMobile());
        userProfileResponse.setRegistred_date(generalUserProfileByEmail.getRegisteredDate().toString());
        if (generalUserProfileByEmail.getIsActive() == 1) {
            userProfileResponse.setStatus(true);
        } else {
            userProfileResponse.setStatus(false);
        }
        userProfileResponse.setCountry_id(generalUserProfileByEmail.getCountry().getId());
        userProfileResponse.setImg(generalUserProfileByEmail.getProfileImg());

        return userProfileResponse;
    }

    @Override
    public UserLoginResponse changeTheStudentPassword(ChangePasswordRequest changePasswordRequest) {

        GeneralUserProfile generalUserProfile = studentAuthentication();
        String oldPassword = changePasswordRequest.getOldPassword();
        String newPassword = changePasswordRequest.getNewPassword();
        String repeatPassword = changePasswordRequest.getRepeatPassword();

        if (oldPassword == null || oldPassword.isEmpty() || newPassword == null || newPassword.isEmpty() || repeatPassword == null || repeatPassword.isEmpty()) {
            throw new ErrorException("Invalid Request", VarList.RSP_NO_DATA_FOUND);
        }

        PasswordEncoderConfig by = new PasswordEncoderConfig();

        if (!by.passwordEncoder().matches(oldPassword, generalUserProfile.getPassword())) {
            throw new ErrorException("Your Old Password is Incorrect", VarList.RSP_NO_DATA_FOUND);
        }

        if (!newPassword.equals(repeatPassword)) {
            throw new ErrorException("Repeat The Password Correctly", VarList.RSP_NO_DATA_FOUND);
        }
        String encodedNewPassword = by.passwordEncoder().encode(newPassword);
        generalUserProfile.setPassword(encodedNewPassword);

        generalUserProfileRepository.save(generalUserProfile);
        UserLoginResponse userLoginResponse = changeToken(generalUserProfile);

        return userLoginResponse;
    }

    @Override
    public UserProfileResponse adminGetUserProfileByEmail() {
        GeneralUserProfile generalUserProfileByEmail = authentication();
        if (generalUserProfileByEmail == null) {
            throw new ErrorException("Invalid User Email", VarList.RSP_NO_DATA_FOUND);
        }

        UserProfileResponse userProfileResponse = new UserProfileResponse();
        userProfileResponse.setFname(generalUserProfileByEmail.getFirstName());
        userProfileResponse.setLname(generalUserProfileByEmail.getLastName());
        userProfileResponse.setEmail(generalUserProfileByEmail.getEmail());
        userProfileResponse.setMobile(generalUserProfileByEmail.getMobile());
        userProfileResponse.setRegistred_date(generalUserProfileByEmail.getRegisteredDate().toString());
        if (generalUserProfileByEmail.getIsActive() == 1) {
            userProfileResponse.setStatus(true);
        } else {
            userProfileResponse.setStatus(false);
        }
        userProfileResponse.setCountry_id(generalUserProfileByEmail.getCountry().getId());
        userProfileResponse.setImg(generalUserProfileByEmail.getProfileImg());

        return userProfileResponse;
    }

    @Override
    public UserLoginResponse adminUpdateProfile(UserUpdateProfileRequest userUpdateProfileRequest) {
        GeneralUserProfile generalUserProfileByEmail = authentication();
        final String firstName = userUpdateProfileRequest.getFname();
        final String lastName = userUpdateProfileRequest.getLname();
        final MultipartFile image = userUpdateProfileRequest.getImg();
        final String email = userUpdateProfileRequest.getEmail();
        final String mobile = userUpdateProfileRequest.getMobile();
        final int country = userUpdateProfileRequest.getCountry();


        if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty() || email == null || email.isEmpty() || mobile == null
                || mobile.isEmpty() || country == 0)
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

        if (generalUserProfileByEmail == null)
            throw new ErrorException("Invalid User Details", VarList.RSP_NO_DATA_FOUND);

        boolean isProfileUpdated = false;

        if (!firstName.equals(generalUserProfileByEmail.getFirstName())) {

            isProfileUpdated = true;
            generalUserProfileByEmail.setFirstName(firstName);

        }

        if (!lastName.equals(generalUserProfileByEmail.getLastName())) {
            isProfileUpdated = true;
            generalUserProfileByEmail.setLastName(lastName);

        }
        if (!email.equals(generalUserProfileByEmail.getEmail())) {
            isProfileUpdated = true;
            GeneralUserProfile alreadyExsitingEmail = generalUserProfileRepository.getGeneralUserProfileByEmail(email);
            if (alreadyExsitingEmail != null) {
                throw new ErrorException("Added Email Already Registred ", VarList.RSP_NO_DATA_FOUND);
            }
            generalUserProfileByEmail.setEmail(email);

        }

        if (!mobile.equals(generalUserProfileByEmail.getMobile())) {
            isProfileUpdated = true;
            generalUserProfileByEmail.setMobile(mobile);

        }


        if (country != generalUserProfileByEmail.getCountry().getId()) {
            isProfileUpdated = true;
            Country countryById = countryRepository.getCountryById(country);
            generalUserProfileByEmail.setCountry(countryById);

        }

        if (image != null && !image.isEmpty()) {
            if (!image.getContentType().startsWith("image/") || !image.getOriginalFilename().matches(".*\\.(jpg|jpeg|png|gif|bmp|webp)$")) {
                throw new ErrorException("Invalid image file type or extension. Only image files are allowed.", VarList.RSP_NO_DATA_FOUND);
            }
            try {
                FileUploadResponse imageUploadResponse = FileUploadUtil.saveFile(image, "profile-images");
                generalUserProfileByEmail.setProfileImg(imageUploadResponse.getUrl());
                isProfileUpdated = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }


        if (isProfileUpdated) {
            generalUserProfileRepository.save(generalUserProfileByEmail);
            return changeToken(generalUserProfileByEmail);

        } else {
            throw new ErrorException("Change Details To Update The Profile", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public UserLoginResponse changeAdminPassword(ChangePasswordRequest changePasswordRequest) {
        GeneralUserProfile generalUserProfile = authentication();
        String oldPassword = changePasswordRequest.getOldPassword();
        String newPassword = changePasswordRequest.getNewPassword();
        String repeatPassword = changePasswordRequest.getRepeatPassword();

        if (oldPassword == null || oldPassword.isEmpty() || newPassword == null || newPassword.isEmpty() || repeatPassword == null || repeatPassword.isEmpty()) {
            throw new ErrorException("Invalid Request", VarList.RSP_NO_DATA_FOUND);
        }

        PasswordEncoderConfig by = new PasswordEncoderConfig();

        if (!by.passwordEncoder().matches(oldPassword, generalUserProfile.getPassword())) {
            throw new ErrorException("Your Old Password is Incorrect", VarList.RSP_NO_DATA_FOUND);
        }

        if (!newPassword.equals(repeatPassword)) {
            throw new ErrorException("Repeat The Password Correctly", VarList.RSP_NO_DATA_FOUND);
        }
        String encodedNewPassword = by.passwordEncoder().encode(newPassword);
        generalUserProfile.setPassword(encodedNewPassword);

        generalUserProfileRepository.save(generalUserProfile);
        UserLoginResponse userLoginResponse = changeToken(generalUserProfile);

        return userLoginResponse;
    }


    @Override
    public SuccessResponse updateUserProfileStatus(String email) {
        authentication();
        if (email == null) {
            throw new ErrorException("Empty User Email", VarList.RSP_NO_DATA_FOUND);
        }
        GeneralUserProfile generalUserProfileByEmail = generalUserProfileRepository.getGeneralUserProfileByEmail(email);
        if (generalUserProfileByEmail == null) {
            throw new ErrorException("Invalid User Email", VarList.RSP_NO_DATA_FOUND);
        }

        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setVariable("200");
        if (generalUserProfileByEmail.getIsActive() == 1) {
            generalUserProfileByEmail.setIsActive((byte) 0);
            successResponse.setMessage("DEACTIVE");
        } else {
            generalUserProfileByEmail.setIsActive((byte) 1);
            successResponse.setMessage("ACTIVE");
        }

        generalUserProfileRepository.save(generalUserProfileByEmail);
        return successResponse;
    }


    public SuccessResponse updateUserByEmail(UserProfileUpdateRequest userProfileUpdateRequest) {
        authentication();
        final String firstName = userProfileUpdateRequest.getFname();
        final String lastName = userProfileUpdateRequest.getLname();
        final MultipartFile image = userProfileUpdateRequest.getImg();
        final String email = userProfileUpdateRequest.getEmail();
        final String mobile = userProfileUpdateRequest.getMobile();
        final String password = userProfileUpdateRequest.getPassword();
        final int country = userProfileUpdateRequest.getCountry();
        final String oldEmail = userProfileUpdateRequest.getIdEmail();

        if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty() || email == null || email.isEmpty() || mobile == null
                || mobile.isEmpty() || country == 0 || oldEmail == null || oldEmail.isEmpty())
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

        GeneralUserProfile generalUserProfileByEmail = generalUserProfileRepository.getGeneralUserProfileByEmail(oldEmail);
        if (generalUserProfileByEmail == null)
            throw new ErrorException("Invalid User Details", VarList.RSP_NO_DATA_FOUND);

        boolean isProfileUpdated = false;

        if (!firstName.equals(generalUserProfileByEmail.getFirstName())) {
            System.out.println(firstName + " " + generalUserProfileByEmail.getFirstName());
            isProfileUpdated = true;
            generalUserProfileByEmail.setFirstName(firstName);

        }

        if (!lastName.equals(generalUserProfileByEmail.getLastName())) {
            isProfileUpdated = true;
            generalUserProfileByEmail.setLastName(lastName);

        }
        if (!email.equals(generalUserProfileByEmail.getEmail())) {
            isProfileUpdated = true;
            generalUserProfileByEmail.setEmail(email);

        }

        if (!mobile.equals(generalUserProfileByEmail.getMobile())) {
            isProfileUpdated = true;
            generalUserProfileByEmail.setMobile(mobile);

        }

        if (!password.isEmpty()) {
            isProfileUpdated = true;
            PasswordEncoderConfig by = new PasswordEncoderConfig();
            String encryptedPwd = by.passwordEncoder().encode(password);
            generalUserProfileByEmail.setPassword(encryptedPwd);

        }


        if (country != generalUserProfileByEmail.getCountry().getId()) {
            isProfileUpdated = true;
            Country countryById = countryRepository.getCountryById(country);
            generalUserProfileByEmail.setCountry(countryById);

        }

        if (image != null && !image.isEmpty()) {
            if (!image.getContentType().startsWith("image/") || !image.getOriginalFilename().matches(".*\\.(jpg|jpeg|png|gif|bmp|webp)$")) {
                throw new ErrorException("Invalid image file type or extension. Only image files are allowed.", VarList.RSP_NO_DATA_FOUND);
            }
            try {
                FileUploadResponse imageUploadResponse = FileUploadUtil.saveFile(image, "profile-images");
                generalUserProfileByEmail.setProfileImg(imageUploadResponse.getUrl());
                isProfileUpdated = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }


        if (isProfileUpdated) {
            SuccessResponse successResponse = new SuccessResponse();
            successResponse.setMessage("Profile updated successfully");
            successResponse.setVariable(VarList.RSP_SUCCESS);
            generalUserProfileRepository.save(generalUserProfileByEmail);
            return successResponse;
        } else {
            throw new ErrorException("Change Details To Update The Profile", VarList.RSP_NO_DATA_FOUND);
        }

    }

    private String token;

    @Override
    public UserLoginResponse updateUserByEmail(UserUpdateProfileRequest userUpdateProfileRequest) {


        GeneralUserProfile generalUserProfileByEmail = studentAuthentication();
        final String firstName = userUpdateProfileRequest.getFname();
        final String lastName = userUpdateProfileRequest.getLname();
        final MultipartFile image = userUpdateProfileRequest.getImg();
        final String email = userUpdateProfileRequest.getEmail();
        final String mobile = userUpdateProfileRequest.getMobile();
        final int country = userUpdateProfileRequest.getCountry();


        if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty() || email == null || email.isEmpty() || mobile == null
                || mobile.isEmpty() || country == 0)
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

        if (generalUserProfileByEmail == null)
            throw new ErrorException("Invalid User Details", VarList.RSP_NO_DATA_FOUND);

        boolean isProfileUpdated = false;

        if (!firstName.equals(generalUserProfileByEmail.getFirstName())) {

            isProfileUpdated = true;
            generalUserProfileByEmail.setFirstName(firstName);

        }

        if (!lastName.equals(generalUserProfileByEmail.getLastName())) {
            isProfileUpdated = true;
            generalUserProfileByEmail.setLastName(lastName);

        }
        if (!email.equals(generalUserProfileByEmail.getEmail())) {
            isProfileUpdated = true;
            GeneralUserProfile alreadyExsitingEmail = generalUserProfileRepository.getGeneralUserProfileByEmail(email);
            if (alreadyExsitingEmail != null) {
                throw new ErrorException("Added Email Already Registred ", VarList.RSP_NO_DATA_FOUND);
            }
            generalUserProfileByEmail.setEmail(email);

        }

        if (!mobile.equals(generalUserProfileByEmail.getMobile())) {
            isProfileUpdated = true;
            generalUserProfileByEmail.setMobile(mobile);

        }


        if (country != generalUserProfileByEmail.getCountry().getId()) {
            isProfileUpdated = true;
            Country countryById = countryRepository.getCountryById(country);
            generalUserProfileByEmail.setCountry(countryById);

        }

        if (image != null && !image.isEmpty()) {
            if (!image.getContentType().startsWith("image/") || !image.getOriginalFilename().matches(".*\\.(jpg|jpeg|png|gif|bmp|webp)$")) {
                throw new ErrorException("Invalid image file type or extension. Only image files are allowed.", VarList.RSP_NO_DATA_FOUND);
            }
            try {
                FileUploadResponse imageUploadResponse = FileUploadUtil.saveFile(image, "profile-images");
                generalUserProfileByEmail.setProfileImg(imageUploadResponse.getUrl());
                isProfileUpdated = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }


        if (isProfileUpdated) {
            generalUserProfileRepository.save(generalUserProfileByEmail);
            return changeToken(generalUserProfileByEmail);

        } else {
            throw new ErrorException("Change Details To Update The Profile", VarList.RSP_NO_DATA_FOUND);
        }

    }

    public UserLoginResponse changeToken(GeneralUserProfile generalUserProfile) {

        UserDetails userDetails = userDetailsServicePassword.loadUserByUsername(generalUserProfile.getEmail());
        if (userDetails == null) {
            throw new ErrorException("Change Details To Update The Profile", VarList.RSP_NO_DATA_FOUND);
        }
        try {
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (Exception e) {
            throw new RuntimeException("Token generation failed");
        }


        UserLoginResponse response = new UserLoginResponse();
        response.setToken(token);
        response.setFname(generalUserProfile.getFirstName());
        response.setLname(generalUserProfile.getLastName());
        response.setEmail(generalUserProfile.getEmail());
        response.setGup_type(generalUserProfile.getGupType().getName());
        return response;
    }

}
