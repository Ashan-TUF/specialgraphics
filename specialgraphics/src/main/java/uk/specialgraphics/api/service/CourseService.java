package uk.specialgraphics.api.service;

import org.springframework.data.domain.Page;
import uk.specialgraphics.api.entity.UserZipFile;
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

    List<UserCourseViewResponse> getAdminViewUserCourseDetailsByCourseCode(String courseCode);

    SuccessResponse addZip(CurriculumItemFileUploadRequest fileUploadRequest);

    SuccessResponse updateZip(CurriculumItemFileUpdateRequest curriculumItemFileUpdateRequest);
    UserQuizesInCurriculumItemResponse getUserQuizesByCurriculumItemCode(String courseCode, String curiyculumCode);

    SuccessResponse studentSubmitMcq(UserMcqRequest userMcqRequest);

    UserPerformeQuizeAndAnswersResponse getUserAnswersForQuizesByCurriculumItemCode(String curiyculumCode);

    UserPerformeQuizeAndAnswersResponse getUserAnswersForQuizesByCurriculumItemCode(String curiyculumCode, String email);

    SuccessResponse updateSectionTitleResponse(UpdateSectionTitleRequest updateSectionTitleRequest);

    SuccessResponse updateSectionCurriItem(UpdateSectionCurriItemRequest updateSectionCurriItemRequest);

SuccessResponse updateVideo(UpdateVideoRequest updateVideoRequest);
}
