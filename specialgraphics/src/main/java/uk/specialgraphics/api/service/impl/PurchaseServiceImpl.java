package uk.specialgraphics.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uk.specialgraphics.api.entity.*;
import uk.specialgraphics.api.exception.ErrorException;
import uk.specialgraphics.api.payload.request.AddPurchasedCoursesRequest;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.payload.response.VerifyStudentOwnACourseResponse;
import uk.specialgraphics.api.payload.response.ViewAllAdminNotMarkedCompleteCourseResponse;
import uk.specialgraphics.api.repository.*;
import uk.specialgraphics.api.service.PurchaseService;
import uk.specialgraphics.api.service.UserProfileService;
import uk.specialgraphics.api.utils.VarList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class PurchaseServiceImpl implements PurchaseService {
    private final UserProfileService userProfileService;
    private final CourseRepository courseRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final StudentHasCourseRepository studentHasCourseRepository;
    private final CouponRepository couponRepository;
    private final GeneralUserProfileRepository generalUserProfileRepository;

    @Autowired
    public PurchaseServiceImpl(UserProfileService userProfileService,
                               CourseRepository courseRepository,
                               PaymentMethodRepository paymentMethodRepository,
                               StudentHasCourseRepository studentHasCourseRepository, CouponRepository couponRepository,GeneralUserProfileRepository generalUserProfileRepository) {
        this.userProfileService = userProfileService;
        this.courseRepository = courseRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.studentHasCourseRepository = studentHasCourseRepository;
        this.couponRepository = couponRepository;
        this.generalUserProfileRepository=generalUserProfileRepository;
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

    private GeneralUserProfile adminAuthentication() {
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

        if (profile.getGupType().getId() != 1)
            throw new ErrorException("You are not a Instructor to this operation", VarList.RSP_NO_DATA_FOUND);
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
        studentHasCourse.setDescription(addPurchasedCoursesRequest.getDetails());
        studentHasCourse.setCourse(course);
        studentHasCourse.setGeneralUserProfile(profile);
        studentHasCourse.setIsComplete((byte) 0);
        studentHasCourse.setAdminStatus((byte) 0);
        studentHasCourseRepository.save(studentHasCourse);

        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setMessage("Purchased successfully");
        successResponse.setVariable(VarList.RSP_SUCCESS);
        return successResponse;
    }

    @Override
    public SuccessResponse addCoursesToStudent(String StudentEmail, String courseCode) {
        GeneralUserProfile adminprofile = adminAuthentication();

        if(StudentEmail==null||StudentEmail.isEmpty()||courseCode==null||courseCode.isEmpty()){
            throw new ErrorException("Invalid Request", VarList.RSP_NO_DATA_FOUND);
        }
        Course course = courseRepository.getCourseByCode(courseCode);
        if(course==null){
            throw new ErrorException("Invalid Course Code", VarList.RSP_NO_DATA_FOUND);
        }
        GeneralUserProfile generalUserProfileByEmail = generalUserProfileRepository.getGeneralUserProfileByEmail(StudentEmail);
        if(generalUserProfileByEmail==null){
            throw new ErrorException("Invalid User", VarList.RSP_NO_DATA_FOUND);
        }

        StudentHasCourse studentHasCourse = studentHasCourseRepository.getStudentHasCourseByCourseAndGeneralUserProfile(course, generalUserProfileByEmail);
        if (studentHasCourse != null){
            throw new ErrorException("User Already Enrolled With The Course", VarList.RSP_NO_DATA_FOUND);

        }

        StudentHasCourse studentHasCourseNewAdd = new StudentHasCourse();
        studentHasCourseNewAdd.setItemCode(UUID.randomUUID().toString());
        studentHasCourseNewAdd.setTotalPrice(0);
        studentHasCourseNewAdd.setBuyDate(new Date());
        studentHasCourseNewAdd.setDescription("Admin Added Course "+adminprofile.getEmail());
        studentHasCourseNewAdd.setCourse(course);
        studentHasCourseNewAdd.setGeneralUserProfile(generalUserProfileByEmail);
        studentHasCourseNewAdd.setIsComplete((byte) 0);
        studentHasCourseNewAdd.setAdminStatus((byte) 0);
        studentHasCourseRepository.save(studentHasCourseNewAdd);

        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setMessage("Course Added successfully");
        successResponse.setVariable(VarList.RSP_SUCCESS);
        return successResponse;

    }

    @Override
    public VerifyStudentOwnACourseResponse verifyStudentOwnCourse(String courseCode, String offerCode) {
        GeneralUserProfile profile = authentication();
        Course course = courseRepository.getCourseByCode(courseCode);
        boolean isVerify = false;
        if (course == null)
            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);

        StudentHasCourse studentHasCourse = studentHasCourseRepository.getStudentHasCourseByCourseAndGeneralUserProfile(course, profile);
        if (studentHasCourse == null)
            isVerify = true;

        VerifyStudentOwnACourseResponse verifyStudentOwnACourseResponse = new VerifyStudentOwnACourseResponse();
        verifyStudentOwnACourseResponse.setVerify(isVerify);

        Coupon couponByUidAndAndCourse = couponRepository.getCouponByUidAndAndCourse(offerCode, course);

        if (couponByUidAndAndCourse == null) {
            verifyStudentOwnACourseResponse.setPrice(course.getPrice());
        } else {
            verifyStudentOwnACourseResponse.setPrice(course.getPrice() - couponByUidAndAndCourse.getPrice());
        }

        return verifyStudentOwnACourseResponse;

    }

    @Override
    public Page<StudentHasCourse> getAllStudentHasCourses(int page, int size) {
        adminAuthentication();
        Pageable pageable = PageRequest.of(page, size);
        return studentHasCourseRepository.getAllStudentHasCourses(pageable);
    }

    @Override
    public List<ViewAllAdminNotMarkedCompleteCourseResponse> getAllNotMarkedCourses() {

        adminAuthentication();
        List<StudentHasCourse> allAdminNotMarkedCompletedCourses = studentHasCourseRepository.getAllAdminNotMarkedCompletedCourses();
        if (allAdminNotMarkedCompletedCourses == null) {
            throw new ErrorException("no Completed Courses", VarList.RSP_NO_DATA_FOUND);
        }
        ArrayList<ViewAllAdminNotMarkedCompleteCourseResponse> viewAllAdminNotMarkedCompleteCourseResponses = new ArrayList<>();

        for (StudentHasCourse studentHasCourse : allAdminNotMarkedCompletedCourses) {
            ViewAllAdminNotMarkedCompleteCourseResponse viewAllAdminNotMarkedCompleteCourseResponse = new ViewAllAdminNotMarkedCompleteCourseResponse();
            viewAllAdminNotMarkedCompleteCourseResponse.setCourse_id(studentHasCourse.getId());
            GeneralUserProfile generalUserProfile = studentHasCourse.getGeneralUserProfile();
            viewAllAdminNotMarkedCompleteCourseResponse.setFname(generalUserProfile.getFirstName());
            viewAllAdminNotMarkedCompleteCourseResponse.setLname(generalUserProfile.getLastName());
            viewAllAdminNotMarkedCompleteCourseResponse.setEmail(generalUserProfile.getEmail());
            viewAllAdminNotMarkedCompleteCourseResponse.setCourse(studentHasCourse.getCourse().getCourseTitle());

            viewAllAdminNotMarkedCompleteCourseResponses.add(viewAllAdminNotMarkedCompleteCourseResponse);
        }
        return viewAllAdminNotMarkedCompleteCourseResponses;
    }

}
