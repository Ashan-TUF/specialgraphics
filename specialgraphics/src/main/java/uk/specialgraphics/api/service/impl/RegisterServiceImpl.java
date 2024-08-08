package uk.specialgraphics.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import uk.specialgraphics.api.config.PasswordEncoderConfig;
import uk.specialgraphics.api.entity.GeneralUserProfile;
import uk.specialgraphics.api.entity.GupType;
import uk.specialgraphics.api.exception.ErrorException;
import uk.specialgraphics.api.payload.request.GeneralUserProfileRequest;
import uk.specialgraphics.api.payload.response.GeneralUserProfileResponse;
import uk.specialgraphics.api.repository.GeneralUserProfileRepository;
import uk.specialgraphics.api.repository.GupTypeRepository;
import uk.specialgraphics.api.security.JwtTokenUtil;
import uk.specialgraphics.api.security.JwtUserDetailsServicePassword;
import uk.specialgraphics.api.service.RegisterService;
import uk.specialgraphics.api.utils.VarList;

import java.util.Date;
import java.util.UUID;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private GeneralUserProfileRepository generalUserProfileRepository;
    @Autowired
    private GupTypeRepository gupTypeRepository;
    @Autowired
    private JwtUserDetailsServicePassword userDetailsServicePassword;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public GeneralUserProfileResponse saveUser(GeneralUserProfileRequest generalUserProfileRequest) {
        String email = generalUserProfileRequest.getEmail();
        String firstName = generalUserProfileRequest.getFirstName();
        String lastName = generalUserProfileRequest.getLastName();
        String password = generalUserProfileRequest.getPassword();

        if (email == null || email.isEmpty() || firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty() || password == null || password.isEmpty()) {
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
        }

        GeneralUserProfile generalUserProfile = generalUserProfileRepository.getGeneralUserProfileByEmail(generalUserProfileRequest.getEmail());

        if (generalUserProfile == null) {

            generalUserProfile = new GeneralUserProfile();
            generalUserProfile.setEmail(email);
            generalUserProfile.setUserCode(UUID.randomUUID().toString());
            generalUserProfile.setFirstName(firstName);
            generalUserProfile.setLastName(lastName);
            PasswordEncoderConfig by = new PasswordEncoderConfig();
            String encryptedPwd = by.passwordEncoder().encode(generalUserProfileRequest.getPassword());
            generalUserProfile.setPassword(encryptedPwd);
            generalUserProfile.setRegisteredDate(new Date());
            generalUserProfile.setIsActive((byte) 1);
            GupType gupTypeObj = gupTypeRepository.getGupTypeById(1);
            if (gupTypeObj == null) {
                throw new ErrorException("Invalid gup type id", VarList.RSP_NO_DATA_FOUND);
            }
            generalUserProfile.setGupType(gupTypeObj);


            generalUserProfileRepository.save(generalUserProfile);

            UserDetails userDetails = userDetailsServicePassword.loadUserByUsername(generalUserProfile.getEmail());
            String token;
            try {
                token = jwtTokenUtil.generateToken(userDetails);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            GeneralUserProfileResponse generalUserProfileResponse = new GeneralUserProfileResponse();

            generalUserProfileResponse.setCode(generalUserProfile.getUserCode());
            generalUserProfileResponse.setToken(token);

            generalUserProfileResponse.setMessage("User profile added successfully");
            generalUserProfileResponse.setVariable(VarList.RSP_SUCCESS);
            return generalUserProfileResponse;


        } else {
            throw new ErrorException("User already exists", VarList.RSP_NO_DATA_FOUND);
        }
    }
}
