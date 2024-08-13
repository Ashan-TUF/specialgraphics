package uk.specialgraphics.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.specialgraphics.api.payload.request.CourseLandingPageRequest;
import uk.specialgraphics.api.payload.request.IntendedLearnersRequest;
import uk.specialgraphics.api.payload.response.CourseLandingPageResponse;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.service.ManageCourseService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "/managecourse")
public class ManageCourseController {
    @Autowired
    private ManageCourseService manageCourseService;
    @PostMapping("/savecourselandingpage")
    public SuccessResponse courseLandingPage(CourseLandingPageRequest courseLandingPageRequest) {
        return manageCourseService.saveCourseLandingPage(courseLandingPageRequest);
    }
    @GetMapping("/getcourselandingpage/{courseCode}")
    public CourseLandingPageResponse GetCourseLandingPage(@PathVariable("courseCode") String courseCode) {
        return manageCourseService.getCourseLandingPage(courseCode);
    }
    @PostMapping("/saveIntendedLearners")
    public SuccessResponse IntendedLearners(IntendedLearnersRequest intendedLearnersRequest) {
        return manageCourseService.saveIntendedLearners(intendedLearnersRequest);
    }
}
