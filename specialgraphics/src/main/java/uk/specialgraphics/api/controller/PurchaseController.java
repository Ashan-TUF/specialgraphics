package uk.specialgraphics.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.specialgraphics.api.entity.GeneralUserProfile;
import uk.specialgraphics.api.entity.StudentHasCourse;
import uk.specialgraphics.api.payload.request.AddPurchasedCoursesRequest;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.payload.response.VerifyStudentOwnACourseResponse;
import uk.specialgraphics.api.payload.response.ViewAllAdminNotMarkedCompleteCourseResponse;
import uk.specialgraphics.api.service.PurchaseService;

import java.util.List;

@RestController
@RequestMapping(value = "payment")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PurchaseController {
    @Autowired
    PurchaseService purchaseService;

    @PostMapping("/addToStudentsPurchasedCourses")
    public SuccessResponse addToStudentsPurchasedCourses(AddPurchasedCoursesRequest addPurchasedCoursesRequest) {
        return purchaseService.addToStudentsPurchasedCourses(addPurchasedCoursesRequest);
    }

    @GetMapping("/verifyStudentOwnCourse/{courseCode}/{offerCode}")
    public VerifyStudentOwnACourseResponse verifyStudentOwnCourse(@PathVariable String courseCode, @PathVariable String offerCode) {
        return purchaseService.verifyStudentOwnCourse(courseCode, offerCode);
    }

    @GetMapping("/adminAddCourseToStudent/{course}/{email}")
    public SuccessResponse adminAddCourseToStudent(@PathVariable String email,@PathVariable String course) {
        return purchaseService.addCoursesToStudent(email, course);
    }

    @GetMapping("/getAllPurchasesDetails")
    public ResponseEntity<Page<StudentHasCourse>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<StudentHasCourse> allStudentHasCourses = purchaseService.getAllStudentHasCourses(page, size);
        return new ResponseEntity<>(allStudentHasCourses, HttpStatus.OK);
    }

    @GetMapping("/getNotAdminMarkedCompletedCourses")
    public List<ViewAllAdminNotMarkedCompleteCourseResponse> getAllNotMarkedCourses() {
        return purchaseService.getAllNotMarkedCourses();
    }


}
