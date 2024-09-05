package uk.specialgraphics.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uk.specialgraphics.api.entity.Course;
import uk.specialgraphics.api.entity.GeneralUserProfile;
import uk.specialgraphics.api.entity.PaymentMethod;
import uk.specialgraphics.api.entity.StudentHasCourse;
import uk.specialgraphics.api.exception.ErrorException;
import uk.specialgraphics.api.payload.request.AddPurchasedCoursesRequest;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.repository.*;
import uk.specialgraphics.api.service.PurchaseService;
import uk.specialgraphics.api.service.UserProfileService;
import uk.specialgraphics.api.utils.VarList;

import java.util.Date;
import java.util.UUID;

@Service
public class PurchaseServiceImpl implements PurchaseService {
    private final UserProfileService userProfileService;
    private final CourseRepository courseRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final StudentHasCourseRepository studentHasCourseRepository;

    @Autowired
    public PurchaseServiceImpl(UserProfileService userProfileService,
                               CourseRepository courseRepository,
                               PaymentMethodRepository paymentMethodRepository,
                               StudentHasCourseRepository studentHasCourseRepository) {
        this.userProfileService = userProfileService;
        this.courseRepository = courseRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.studentHasCourseRepository = studentHasCourseRepository;
    }

    private GeneralUserProfile authentication() {
        Authentication authentication;
        String username;
        GeneralUserProfile profile;
        authentication = SecurityContextHolder.getContext().getAuthentication();
        username = authentication.getName();
        profile = userProfileService.getProfile(username);

        if (profile == null)
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);

        if (profile.getIsActive() != 1)
            throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);

        if (profile.getGupType().getId() != 2)
            throw new ErrorException("You are not a student to this operation", VarList.RSP_NO_DATA_FOUND);
        return profile;
    }

    @Override
    public SuccessResponse addToStudentsPurchasedCourses(AddPurchasedCoursesRequest addPurchasedCoursesRequest) {
        final Integer paymentMethodId = addPurchasedCoursesRequest.getPaymentMethodId();
        final String courseCode = addPurchasedCoursesRequest.getCourseCode();
        final Double totalPrice = addPurchasedCoursesRequest.getTotalPrice();
        GeneralUserProfile profile = authentication();

        if (paymentMethodId == null || paymentMethodId.toString().isEmpty() || courseCode == null || courseCode.isEmpty() || totalPrice == null || totalPrice.toString().isEmpty())
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
        PaymentMethod paymentMethod = paymentMethodRepository.getPaymentMethodById(paymentMethodId);
        if (paymentMethod == null)
            throw new ErrorException("Invalid payment method id", VarList.RSP_NO_DATA_FOUND);
        Course course = courseRepository.getCourseByCode(courseCode);
        if (course == null)
            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);

        StudentHasCourse studentHasCourse = studentHasCourseRepository.getStudentHasCourseByCourseAndGeneralUserProfile(course, profile);
        if (studentHasCourse != null)
            throw new ErrorException("The student has already purchased this course", VarList.RSP_NO_DATA_FOUND);

        studentHasCourse = new StudentHasCourse();
        studentHasCourse.setItemCode(UUID.randomUUID().toString());
        studentHasCourse.setTotalPrice(totalPrice);
        studentHasCourse.setBuyDate(new Date());
        studentHasCourse.setPaymentMethod(paymentMethod);
        studentHasCourse.setCourse(course);
        studentHasCourse.setGeneralUserProfile(profile);
        studentHasCourseRepository.save(studentHasCourse);

        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setMessage("Purchased successfully");
        successResponse.setVariable(VarList.RSP_SUCCESS);
        return successResponse;
    }
}
