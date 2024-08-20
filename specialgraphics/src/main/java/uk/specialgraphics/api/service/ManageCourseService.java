package uk.specialgraphics.api.service;

import uk.specialgraphics.api.payload.request.CourseLandingPageRequest;
import uk.specialgraphics.api.payload.request.IntendedLearnersRequest;
import uk.specialgraphics.api.payload.response.CourseLandingPageResponse;
import uk.specialgraphics.api.payload.response.SuccessResponse;

public interface ManageCourseService {
    SuccessResponse saveCourseLandingPage(CourseLandingPageRequest courseLandingPageRequest);

    CourseLandingPageResponse getCourseLandingPage(String courseCode);

    SuccessResponse saveIntendedLearners(IntendedLearnersRequest intendedLearnersRequest);
}
