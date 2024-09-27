package uk.specialgraphics.api.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.specialgraphics.api.config.PasswordEncoderConfig;
import uk.specialgraphics.api.entity.Country;
import uk.specialgraphics.api.entity.GeneralUserProfile;
import uk.specialgraphics.api.exception.ErrorException;
import uk.specialgraphics.api.payload.request.ProfileUpdateRequest;
import uk.specialgraphics.api.payload.response.FileUploadResponse;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.repository.CountryRepository;
import uk.specialgraphics.api.repository.GeneralUserProfileRepository;
import uk.specialgraphics.api.service.ProfileService;
import uk.specialgraphics.api.service.UserProfileService;
import uk.specialgraphics.api.utils.FileUploadUtil;
import uk.specialgraphics.api.utils.VarList;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

@Service
public class ProfileServiceImpl implements ProfileService {
    private final UserProfileService userProfileService;
    private final CountryRepository countryRepository;
    private final GeneralUserProfileRepository generalUserProfileRepository;

    public ProfileServiceImpl(UserProfileService userProfileService, CountryRepository countryRepository, GeneralUserProfileRepository generalUserProfileRepository) {
        this.userProfileService = userProfileService;
        this.countryRepository = countryRepository;
        this.generalUserProfileRepository = generalUserProfileRepository;
    }

    @Override
    public SuccessResponse updateProfile(ProfileUpdateRequest profileUpdateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileService.getProfile(username);
        if (profile == null)
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        if (profile.getIsActive() != 1)
            throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
        final String email = profileUpdateRequest.getEmail();
        final String firstName = profileUpdateRequest.getFirstName();
        final String lastName = profileUpdateRequest.getLastName();
        final String password = profileUpdateRequest.getPassword();
        final String mobile = profileUpdateRequest.getMobile();
        final Integer country = profileUpdateRequest.getCountry();
        final MultipartFile profileImage = profileUpdateRequest.getProfileImage();

        if (email != null && !email.isEmpty())
            profile.setEmail(email);
        if (firstName != null && !firstName.isEmpty())
            profile.setFirstName(firstName);
        if (lastName != null && !lastName.isEmpty())
            profile.setLastName(lastName);
        if (password != null && !password.isEmpty()) {
            PasswordEncoderConfig by = new PasswordEncoderConfig();
            String encryptedPwd = by.passwordEncoder().encode(password);
            profile.setPassword(encryptedPwd);
        }
        if (mobile != null && !mobile.isEmpty())
            profile.setMobile(mobile);
        if (country != null && !country.toString().isEmpty()) {
            Country countryObj = countryRepository.getCountryById(country);
            if (countryObj == null)
                throw new ErrorException("Country not found", VarList.RSP_NO_DATA_FOUND);
            profile.setCountry(countryObj);
        }
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                FileUploadResponse profileImg = FileUploadUtil.saveFile(profileImage, "profile-images");
                if (profile.getProfileImg() != null && !profile.getProfileImg().isEmpty()) {
                    FileUploadUtil.deleteFile(profile.getProfileImg());
                }
                profile.setProfileImg(profileImg.getFilename());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        generalUserProfileRepository.save(profile);

        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setMessage("Your profile update is successful");
        successResponse.setVariable(VarList.RSP_SUCCESS);
        return successResponse;
    }
}
