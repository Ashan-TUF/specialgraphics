package uk.specialgraphics.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.specialgraphics.api.payload.request.AddPurchasedCoursesRequest;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.service.PurchaseService;

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
    @GetMapping("/verifyStudentOwnCourse/{courseCode}")
    public boolean verifyStudentOwnCourse(@PathVariable String courseCode) {
        return purchaseService.verifyStudentOwnCourse(courseCode);
    }
}
