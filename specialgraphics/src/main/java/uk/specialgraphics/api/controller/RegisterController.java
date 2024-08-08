package uk.specialgraphics.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.specialgraphics.api.payload.request.GeneralUserProfileRequest;
import uk.specialgraphics.api.payload.response.GeneralUserProfileResponse;
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
}
