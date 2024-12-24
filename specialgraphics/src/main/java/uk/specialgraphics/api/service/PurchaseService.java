package uk.specialgraphics.api.service;

import uk.specialgraphics.api.payload.request.AddPurchasedCoursesRequest;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.payload.response.VerifyStudentOwnACourseResponse;

public interface PurchaseService {
    SuccessResponse addToStudentsPurchasedCourses(AddPurchasedCoursesRequest addPurchasedCoursesRequest);

    VerifyStudentOwnACourseResponse verifyStudentOwnCourse(String courseCode, String offerCode);
}
