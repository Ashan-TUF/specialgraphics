package uk.specialgraphics.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.specialgraphics.api.payload.request.UserLoginRequset;
import uk.specialgraphics.api.payload.response.UserLoginResponse;
import uk.specialgraphics.api.service.LoginService;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/authentication")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/student")
    public UserLoginResponse userLoginResponse(UserLoginRequset request, HttpServletResponse response) throws Exception {
        UserLoginResponse loginSessionResponse = loginService.userLoginWithPassword(request,response);
        return loginSessionResponse;
    }

    @PostMapping("/admin")
    public UserLoginResponse adminLogIn(UserLoginRequset request,HttpServletResponse response) throws Exception{
        UserLoginResponse adminLoginResponse = loginService.adminLoginWithPassword(request,response);
        return adminLoginResponse;
    }


}
