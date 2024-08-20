package uk.specialgraphics.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import uk.specialgraphics.api.config.PasswordEncoderConfig;
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
    @Autowired
    GeneralUserProfileRepository generalUserProfileRepostory;
    @Autowired
    private JwtUserDetailsServicePassword userDetailsServicePassword;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Override
    public UserLoginResponse userLoginWithPassword(UserLoginRequset request) {
        UserLoginResponse userLoginResponse = new UserLoginResponse();

        GeneralUserProfile gup = generalUserProfileRepostory.getGeneralUserProfileByEmail(request.getEmail());

        if (gup != null) {

            if (new BCryptPasswordEncoder().matches(request.getPassword(), gup.getPassword())) {

                UserDetails userDetails = userDetailsServicePassword.loadUserByUsername(request.getEmail());

                String token = null;
                try {
                    token = jwtTokenUtil.generateToken(userDetails);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                userLoginResponse.setToken(token);
                userLoginResponse.setFname(gup.getFirstName());
                userLoginResponse.setLname(gup.getLastName());
                userLoginResponse.setEmail(gup.getEmail());
                userLoginResponse.setGup_type(gup.getGupType().getName());
            } else {
                log.warn("password incorrect.");
                throw new ErrorException("incorrect password.", VarList.RSP_NO_DATA_FOUND);
            }

        } else {
            log.warn("user not found");
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }

        return userLoginResponse;
    }
}
