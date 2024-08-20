package uk.specialgraphics.api.service;

import uk.specialgraphics.api.payload.request.CourseRequest;
import uk.specialgraphics.api.payload.response.SuccessResponse;

public interface CourseService {
    SuccessResponse addCourse(CourseRequest courseRequest);
}
