package uk.specialgraphics.api.service;

import uk.specialgraphics.api.payload.request.*;
import uk.specialgraphics.api.payload.response.*;

import java.util.List;

public interface CourseService {
    SuccessResponse addCourse(CourseRequest courseRequest);

    List<CourseResponse> getAllCourses();

    CourseResponse getCourseByCode(String courseCode);

    SuccessResponse updateCourseByCode(CourseRequest request);

    AddCourseSectionResponse addSection(AddSectionRequest addSectionRequest);

    AddSectionCurriculumItemResponse addSectionItem(AddSectionCurriculumItemRequest addSectionCurriculumItemRequest);

    SuccessResponse addVideo(AddVideoRequest addVideoRequest);

    CourseSectionResponse getCurriculumItemsBySectionCode(String sectionCode);

    List<CourseSectionResponse> getCourseSectionsByCourseCode(String courseCode);

    SuccessResponse addNewQuiz(String curriculumItemCode);


    SuccessResponse AddNewQuizeItem(AddQuizeItemRequest addQuizeItemRequest);

    QuizesInCurriculumItemResponse getQuizesByCurriculumItemCode(String curiyculumCode);

    SuccessResponse updateNewQuizeItem(UpdateQuizeItemRequest updateQuizeItemRequest);

    GetCourseDetailsByCourseCodeResponse getCourseDetailsByCourseCode(String courseCode);

    List<UserCourseResponse> getAllUserCourses();

    UserCourseViewResponse getUserCourseDetailsByCourseCode(String courseCode);

    SuccessResponse addZip(CurriculumItemFileUploadRequest fileUploadRequest);

    UserQuizesInCurriculumItemResponse getUserQuizesByCurriculumItemCode(String courseCode,String curiyculumCode);

    SuccessResponse studentSubmitMcq(UserMcqRequest userMcqRequest);

    UserPerformeQuizeAndAnswersResponse getUserAnswersForQuizesByCurriculumItemCode(String curiyculumCode);
}
