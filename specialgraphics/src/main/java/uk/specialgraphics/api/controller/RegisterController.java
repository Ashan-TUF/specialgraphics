package uk.specialgraphics.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.specialgraphics.api.payload.request.ForgotPasswordRequest;
import uk.specialgraphics.api.payload.request.GeneralUserProfileRequest;
import uk.specialgraphics.api.payload.response.GeneralUserProfileResponse;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.service.RegisterService;

@RestController
@RequestMapping("/register")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RegisterController {
    @Autowired
    private RegisterService registerService;
    @PostMapping("/add")
    public GeneralUserProfileResponse add(GeneralUserProfileRequest generalUserProfileRequest) {
        return registerService.saveUser(generalUserProfileRequest);
    }

    @GetMapping("/verify/{email}/{code}")
    public SuccessResponse verify(@PathVariable String email,@PathVariable String code) {
        return registerService.verifyUser(email, code);
    }

    @GetMapping("/vericodeforgotpassword/{email}")
    public SuccessResponse verificationCodeForgotPassword(@PathVariable String email) {
        return registerService.sendVerificatioCode(email);
    }
    @PostMapping("/forgotPassword")
    public SuccessResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        return registerService.changeTheUserPassword(forgotPasswordRequest);
    }




}
