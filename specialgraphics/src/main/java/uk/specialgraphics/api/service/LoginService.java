package uk.specialgraphics.api.service;

import uk.specialgraphics.api.payload.request.UserLoginRequset;
import uk.specialgraphics.api.payload.response.UserLoginResponse;

public interface LoginService {
    UserLoginResponse userLoginWithPassword(UserLoginRequset request);

    UserLoginResponse adminLoginWithPassword(UserLoginRequset request);

}
