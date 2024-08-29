package uk.specialgraphics.api.service;

import uk.specialgraphics.api.payload.request.AddSectionCurriculumItemRequest;
import uk.specialgraphics.api.payload.request.AddSectionRequest;
import uk.specialgraphics.api.payload.request.AddVideoRequest;
import uk.specialgraphics.api.payload.request.CourseRequest;
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

    List<CurriculumItemResponse> getCurriculumItemsBySectionCode(String sectionCode);

    List<CourseSectionResponse> getCourseSectionsByCourseCode(String courseCode);
}
