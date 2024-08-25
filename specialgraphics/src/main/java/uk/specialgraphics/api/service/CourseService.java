package uk.specialgraphics.api.service;

import uk.specialgraphics.api.payload.request.CourseRequest;
import uk.specialgraphics.api.payload.request.SingleCourseRequest;
import uk.specialgraphics.api.payload.response.AllCourseResponse;
import uk.specialgraphics.api.payload.response.SingleCourseResponse;
import uk.specialgraphics.api.payload.response.SuccessResponse;

public interface CourseService {
    SuccessResponse addCourse(CourseRequest courseRequest);

    AllCourseResponse getAllCourses();

SingleCourseResponse getCourseByCode(SingleCourseRequest code);

    SuccessResponse updateCourseByCode(CourseRequest request);
}
