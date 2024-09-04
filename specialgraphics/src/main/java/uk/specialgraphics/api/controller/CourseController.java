package uk.specialgraphics.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.specialgraphics.api.payload.request.*;
import uk.specialgraphics.api.payload.response.*;
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

    @GetMapping("/getCourseByCode/{courseCode}")
    public CourseResponse getCourseById(@PathVariable String courseCode) {
        return courseService.getCourseByCode(courseCode);
    }

    @PutMapping("/updateCourse")
    public SuccessResponse updateCourse(CourseRequest request) {
        return courseService.updateCourseByCode(request);
    }

    @PostMapping("/addSection")
    public AddCourseSectionResponse AddSection(AddSectionRequest addSectionRequest) {
        return courseService.addSection(addSectionRequest);
    }

    @PostMapping("/addSectionItem")
    public AddSectionCurriculumItemResponse AddSection(AddSectionCurriculumItemRequest addSectionCurriculumItemRequest) {
        return courseService.addSectionItem(addSectionCurriculumItemRequest);
    }

    @PostMapping("/addVideo")
    public SuccessResponse Addvideo(AddVideoRequest addVideoRequest) {
        return courseService.addVideo(addVideoRequest);
    }

    @GetMapping("/getCurriculumItemsBySectionCode/{sectionCode}")
    public CourseSectionResponse getCurriculumItemsBySectionCode(@PathVariable String sectionCode) {
        return courseService.getCurriculumItemsBySectionCode(sectionCode);
    }

    @GetMapping("/getCourseSectionsByCourseCode/{courseCode}")
    public List<CourseSectionResponse> getCourseSectionsByCourseCode(@PathVariable String courseCode) {
        return courseService.getCourseSectionsByCourseCode(courseCode);
    }

    @GetMapping("/addQuize/{curriculumItemCode}")
    public SuccessResponse addQuize(@PathVariable String curriculumItemCode) {
        return courseService.addNewQuiz(curriculumItemCode);
    }
    @PostMapping("/addQuizItem")
    public SuccessResponse addQuizeItem(AddQuizeItemRequest addQuizeItemRequest){
        return  courseService.AddNewQuizeItem(addQuizeItemRequest);
    }
    @GetMapping("/getQuizItemsByCurriculumItemCode/{curriculumItemCode}")
    public QuizesInCurriculumItemResponse getQuizItemsByCurriculumItemCode(@PathVariable String curriculumItemCode) {
        return courseService.getQuizesByCurriculumItemCode(curriculumItemCode);
    }
}
