package uk.specialgraphics.api.service;

import org.springframework.data.domain.Page;
import uk.specialgraphics.api.entity.StudentHasCourse;
import uk.specialgraphics.api.payload.request.AddPurchasedCoursesRequest;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.payload.response.VerifyStudentOwnACourseResponse;
import uk.specialgraphics.api.payload.response.ViewAllAdminNotMarkedCompleteCourseResponse;

import java.util.List;

public interface PurchaseService {
    SuccessResponse addToStudentsPurchasedCourses(AddPurchasedCoursesRequest addPurchasedCoursesRequest);

    VerifyStudentOwnACourseResponse verifyStudentOwnCourse(String courseCode, String offerCode);

    Page<StudentHasCourse> getAllStudentHasCourses(int page, int size);

List<ViewAllAdminNotMarkedCompleteCourseResponse> getAllNotMarkedCourses();
}
