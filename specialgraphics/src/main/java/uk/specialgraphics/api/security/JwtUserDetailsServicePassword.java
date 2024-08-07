package uk.specialgraphics.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uk.specialgraphics.api.entity.GeneralUserProfile;
import uk.specialgraphics.api.exception.ErrorException;
import uk.specialgraphics.api.repository.GeneralUserProfileRepository;
import uk.specialgraphics.api.utils.VarList;

import java.util.ArrayList;

@Service
public class JwtUserDetailsServicePassword implements UserDetailsService {

    @Autowired
    GeneralUserProfileRepository generalUserProfileRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        GeneralUserProfile gup = generalUserProfileRepository.getGeneralUserProfileByEmail(username);
        if (gup != null) {
            User user = new User(username, gup.getPassword(), new ArrayList<>());
            if (user != null) {
                return user;
            } else {
                throw new ErrorException("Your password is incorrect.",
                        VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }

}
