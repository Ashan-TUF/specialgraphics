package uk.specialgraphics.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import uk.specialgraphics.api.entity.GeneralUserProfile;
import uk.specialgraphics.api.exception.ErrorException;
import uk.specialgraphics.api.payload.request.UserLoginRequset;
import uk.specialgraphics.api.payload.response.UserLoginResponse;
import uk.specialgraphics.api.repository.GeneralUserProfileRepository;
import uk.specialgraphics.api.security.JwtTokenUtil;
import uk.specialgraphics.api.security.JwtUserDetailsServicePassword;
import uk.specialgraphics.api.service.LoginService;
import uk.specialgraphics.api.utils.VarList;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    GeneralUserProfileRepository generalUserProfileRepository;
    private JwtUserDetailsServicePassword userDetailsServicePassword;
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    public LoginServiceImpl(GeneralUserProfileRepository generalUserProfileRepository, JwtUserDetailsServicePassword userDetailsServicePassword, JwtTokenUtil jwtTokenUtil) {
        this.generalUserProfileRepository = generalUserProfileRepository;
        this.userDetailsServicePassword = userDetailsServicePassword;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UserDetails userDetails;
    private UserLoginResponse userLoginResponse;
    private String token;

    @Override
    public UserLoginResponse userLoginWithPassword(UserLoginRequset request) {
        final String email = request.getEmail();
        final String password = request.getPassword();

        if (email == null || email.isEmpty() || password == null || password.isEmpty())
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

        GeneralUserProfile gup = generalUserProfileRepository.getGeneralUserProfileByEmail(email);

        if (gup == null)
            throw new ErrorException("Invalid credentials", VarList.RSP_NO_DATA_FOUND);


        UserDetails userDetails = loadUserDetailsByUsername(gup, password, email);

        try {
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (Exception e) {
            throw new RuntimeException("Token generation failed");
        }

        return createUserLoginResponse(gup, token);
    }

    @Override
    public UserLoginResponse adminLoginWithPassword(UserLoginRequset request) {
        final String email = request.getEmail();
        final String password = request.getPassword();

        if (email == null || email.isEmpty() || password == null || password.isEmpty())
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

        GeneralUserProfile gup = generalUserProfileRepository.getGeneralUserProfileByEmail(email);

        if (gup == null)
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);


        if (gup.getGupType().getId() != 1)
            throw new ErrorException("Access denied", VarList.RSP_NO_DATA_FOUND);

        if (gup.getIsActive() != 1)
            throw new ErrorException("Your admin account deactivated.", VarList.RSP_NO_DATA_FOUND);

        UserDetails userDetails = loadUserDetailsByUsername(gup, password, email);

        try {
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (Exception e) {
            throw new RuntimeException("Token generation failed");
        }

        return createUserLoginResponse(gup, token);
    }

    private UserDetails loadUserDetailsByUsername(GeneralUserProfile gup, String password, String email) {
        if (!passwordEncoder.matches(password, gup.getPassword()))
            throw new ErrorException("Invalid credentials", VarList.RSP_NO_DATA_FOUND);

        return userDetailsServicePassword.loadUserByUsername(email);
    }

    private UserLoginResponse createUserLoginResponse(GeneralUserProfile gup, String token) {
        UserLoginResponse response = new UserLoginResponse();
        response.setToken(token);
        response.setFname(gup.getFirstName());
        response.setLname(gup.getLastName());
        response.setEmail(gup.getEmail());
        response.setGup_type(gup.getGupType().getName());
        return response;
    }
}