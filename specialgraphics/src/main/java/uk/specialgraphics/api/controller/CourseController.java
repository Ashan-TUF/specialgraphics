package uk.specialgraphics.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.specialgraphics.api.payload.request.CourseRequest;
import uk.specialgraphics.api.payload.request.SingleCourseRequest;
import uk.specialgraphics.api.payload.response.AllCourseResponse;
import uk.specialgraphics.api.payload.response.SingleCourseResponse;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.service.CourseService;

@RestController
@RequestMapping("/course")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @PostMapping("/addCourse")
    public SuccessResponse addCourse(CourseRequest courseRequest) {
        return courseService.addCourse(courseRequest);
    }

    @PostMapping("/getAllCourses")
    public AllCourseResponse getAllCourses() {
        return courseService.getAllCourses();
    }

    @PostMapping("/getCourseByCode")
    public SingleCourseResponse getCourseById(SingleCourseRequest singleCourseRequest){
        return courseService.getCourseByCode(singleCourseRequest);
    }
    @PostMapping("/updateCourse")
    public SuccessResponse updateCourse(CourseRequest request){
        return courseService.updateCourseByCode(request);
    }

}
