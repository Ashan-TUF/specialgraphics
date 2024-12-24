package uk.specialgraphics.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.specialgraphics.api.payload.response.UserProfileResponse;
import uk.specialgraphics.api.service.UserProfileService;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class AdminController {


    @Autowired
    UserProfileService userProfileService;

    @GetMapping("/getAllDashBoardDetails")
    public void getAllDashBoardDetails() {

    }

}
