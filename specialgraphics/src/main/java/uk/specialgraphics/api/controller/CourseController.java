package uk.specialgraphics.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.specialgraphics.api.payload.request.CourseRequest;
import uk.specialgraphics.api.payload.response.CourseResponse;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.service.CourseService;

import java.util.List;

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
    public List<CourseResponse> getAllCourses() {
        return courseService.getAllCourses();
    }

    @PostMapping("/getCourseByCode/{courseCode}")
    public CourseResponse getCourseById(@PathVariable String courseCode){
        return courseService.getCourseByCode(courseCode);
    }
    @PutMapping("/updateCourse")
    public SuccessResponse updateCourse(CourseRequest request){
        return courseService.updateCourseByCode(request);
    }

}
