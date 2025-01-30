package uk.specialgraphics.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import uk.specialgraphics.api.entity.UserZipFile;
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
    public SuccessResponse addQuizeItem(AddQuizeItemRequest addQuizeItemRequest) {
        return courseService.AddNewQuizeItem(addQuizeItemRequest);
    }

    @GetMapping("/getQuizItemsByCurriculumItemCode/{curriculumItemCode}")
    public QuizesInCurriculumItemResponse getQuizItemsByCurriculumItemCode(@PathVariable String curriculumItemCode) {
        return courseService.getQuizesByCurriculumItemCode(curriculumItemCode);
    }

    @PutMapping("/updateQuizItem")
    public SuccessResponse updateQuizeItem(UpdateQuizeItemRequest updateQuizeItemRequest) {
        return courseService.updateNewQuizeItem(updateQuizeItemRequest);
    }

    @GetMapping("/getCourseDetailsByCourseCode/{courseCode}")
    public GetCourseDetailsByCourseCodeResponse getCourseDetailsByCourseCode(@PathVariable String courseCode) {
        return courseService.getCourseDetailsByCourseCode(courseCode);
    }

    @GetMapping("/getAllUserCourseDetails")
    public List<UserCourseResponse> getAllUserCourseDetails() {
        return courseService.getAllUserCourses();
    }

    @GetMapping("/getUserCourseDetailsByCourseCode/{courseCode}")
    public UserCourseViewResponse getUserCourseDetailsByCourseCode(@PathVariable String courseCode) {
        return courseService.getUserCourseDetailsByCourseCode(courseCode);
    }

    @GetMapping("/getAdminUserCourseDetailsByEmail/{email}")
    public List<UserCourseViewResponse> getAdminViewUserCourseDetailsByCourseCode(@PathVariable String email) {
        return courseService.getAdminViewUserCourseDetailsByCourseCode(email);
    }

    @PostMapping("/addFile")
    public SuccessResponse addFile(CurriculumItemFileUploadRequest fileUploadRequest) {
        return courseService.addZip(fileUploadRequest);
    }

    @PutMapping("/updateFile")
    public SuccessResponse update(CurriculumItemFileUpdateRequest curriculumItemFileUpdateRequest) {
        return courseService.updateZip(curriculumItemFileUpdateRequest);
    }
    @GetMapping("/getUserQuizItemsByCurriculumItemCode/{courseCode}/{curriculumItemCode}")
    public UserQuizesInCurriculumItemResponse getUserMcqQuestions(@PathVariable String courseCode,@PathVariable String curriculumItemCode) {
        return courseService.getUserQuizesByCurriculumItemCode(courseCode,curriculumItemCode);
    }

    @PostMapping("/userSubmitMcqItem")
    public SuccessResponse submitMcq(@RequestBody UserMcqRequest userMcqRequest) {
        return courseService.studentSubmitMcq(userMcqRequest);
    }

    @GetMapping("/getUserQuizItemsAndAnswersByCurriculumItemCode/{curriculumItemCode}")
    public UserPerformeQuizeAndAnswersResponse getUserMcqQuestionsAndAnswers(@PathVariable String curriculumItemCode) {
        return courseService.getUserAnswersForQuizesByCurriculumItemCode(curriculumItemCode);
    }

    @GetMapping("/getUserQuizItemsAndAnswersByCurriculumItemCodeViewAdmin/{curriculumItemCode}/{email}")
    public UserPerformeQuizeAndAnswersResponse getUserMcqQuestionsAndAnswers(@PathVariable String curriculumItemCode,@PathVariable String email) {
        return courseService.getUserAnswersForQuizesByCurriculumItemCode(curriculumItemCode,email);
    }


    @PutMapping("/updateSectionName")
    public SuccessResponse updateSectionName(UpdateSectionTitleRequest updateSectionTitleRequest) {
        return courseService.updateSectionTitleResponse(updateSectionTitleRequest);
    }

    @PutMapping("/updateSectionCurriculamItem")
    public SuccessResponse updateSectionCurriculamItem(UpdateSectionCurriItemRequest updateSectionCurriItemRequest) {
        return courseService.updateSectionCurriItem(updateSectionCurriItemRequest);
    }


    @PutMapping("/updateVideo")
    public SuccessResponse updateVideo(UpdateVideoRequest updateVideoRequest) {
        return courseService.updateVideo(updateVideoRequest);
    }


}
