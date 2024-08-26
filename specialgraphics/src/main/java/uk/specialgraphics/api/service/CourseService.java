package uk.specialgraphics.api.service;

import uk.specialgraphics.api.payload.request.CourseRequest;
import uk.specialgraphics.api.payload.response.CourseResponse;
import uk.specialgraphics.api.payload.response.SuccessResponse;

import java.util.List;

public interface CourseService {
    SuccessResponse addCourse(CourseRequest courseRequest);

    List<CourseResponse> getAllCourses();

    CourseResponse getCourseByCode(String courseCode);

    SuccessResponse updateCourseByCode(CourseRequest request);
}
