package uk.specialgraphics.api.service;

import uk.specialgraphics.api.payload.request.UserLoginRequset;
import uk.specialgraphics.api.payload.response.UserLoginResponse;

import javax.servlet.http.HttpServletResponse;

public interface LoginService {
    UserLoginResponse userLoginWithPassword(UserLoginRequset request, HttpServletResponse response);

    UserLoginResponse adminLoginWithPassword(UserLoginRequset request,HttpServletResponse response);

}
