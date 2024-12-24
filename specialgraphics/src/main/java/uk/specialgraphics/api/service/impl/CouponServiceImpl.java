package uk.specialgraphics.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uk.specialgraphics.api.entity.Country;
import uk.specialgraphics.api.entity.Coupon;
import uk.specialgraphics.api.entity.Course;
import uk.specialgraphics.api.entity.GeneralUserProfile;
import uk.specialgraphics.api.exception.ErrorException;
import uk.specialgraphics.api.payload.request.AddCouponRequest;
import uk.specialgraphics.api.payload.request.UserCouponOfferRequest;
import uk.specialgraphics.api.payload.response.AllCountryResponse;
import uk.specialgraphics.api.payload.response.CouponResponse;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.payload.response.UserCouponResponse;
import uk.specialgraphics.api.repository.CountryRepository;
import uk.specialgraphics.api.repository.CouponRepository;
import uk.specialgraphics.api.repository.CourseRepository;
import uk.specialgraphics.api.service.CountryService;
import uk.specialgraphics.api.service.CouponService;
import uk.specialgraphics.api.service.UserProfileService;
import uk.specialgraphics.api.utils.VarList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class CouponServiceImpl implements CouponService {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    UserProfileService userProfileService;

    private void authentication() {
        Authentication authentication;
        String username;
        GeneralUserProfile profile;
        authentication = SecurityContextHolder.getContext().getAuthentication();
        username = authentication.getName();
        profile = userProfileService.getProfile(username);

        if (profile == null) throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);

        if (profile.getIsActive() != 1) throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);

        if (profile.getGupType().getId() != 1)
            throw new ErrorException("You are not a instructor to this operation", VarList.RSP_NO_DATA_FOUND);
    }


    private GeneralUserProfile userAuthentication() {
        Authentication authentication;
        String username;
        GeneralUserProfile profile;
        authentication = SecurityContextHolder.getContext().getAuthentication();
        username = authentication.getName();
        profile = userProfileService.getProfile(username);

        if (profile == null) throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);

        if (profile.getIsActive() != 1) throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);

        if (profile.getGupType().getId() != 2)
            throw new ErrorException("You are not a Student to this operation", VarList.RSP_NO_DATA_FOUND);
        return profile;
    }

    @Override
    public SuccessResponse addNewCoupon(AddCouponRequest addCouponRequest) {
        authentication();
        if (addCouponRequest.getCourseCode() == null || addCouponRequest.getPrice() == 0) {
            throw new ErrorException("Empty details", VarList.RSP_NO_DATA_FOUND);
        }
        Course course = courseRepository.getCourseByCode(addCouponRequest.getCourseCode());
        Coupon coupon = new Coupon();
        coupon.setCourse(course);
        coupon.setPrice(addCouponRequest.getPrice());
        coupon.setIsActive((byte) 1);
        String uid = "SpecialGraphics-" + UUID.randomUUID().toString();
        coupon.setUid(uid);
        couponRepository.save(coupon);
        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setVariable("200");
        successResponse.setMessage("Coupon Added Success");
        return successResponse;
    }

    @Override
    public List<CouponResponse> getAllCoupons() {
        authentication();
        List<Coupon> all = couponRepository.findAll();
        List<CouponResponse> couponRespons = new ArrayList<>();
        for (Coupon coupon : all) {
            CouponResponse couponResponse = new CouponResponse();
            if(coupon.getIsActive()==1){
                couponResponse.setActive(true);
            }else{
                couponResponse.setActive(false);

            }
            couponResponse.setCourseName(coupon.getCourse().getCourseTitle());
            couponResponse.setUid(coupon.getUid());
            couponResponse.setPrice(coupon.getPrice());
            couponRespons.add(couponResponse);
        }
        return couponRespons;
    }

    @Override
    public UserCouponResponse userGetOffer(UserCouponOfferRequest userCouponOfferRequest) {

        userAuthentication();
        if (userCouponOfferRequest.getCourseCode() == null || userCouponOfferRequest.getCoupon() ==null) {
            throw new ErrorException("Empty details", VarList.RSP_NO_DATA_FOUND);
        }
        Course courseByCode = courseRepository.getCourseByCode(userCouponOfferRequest.getCourseCode());
        if(courseByCode==null){
            throw new ErrorException("Invalid Course Id", VarList.RSP_NO_DATA_FOUND);
        }
        Coupon couponByUidAndAndCourse = couponRepository.getCouponByUidAndAndCourse(userCouponOfferRequest.getCoupon(), courseByCode);
        if(couponByUidAndAndCourse==null){
            throw new ErrorException("Invalid Coupon", VarList.RSP_NO_DATA_FOUND);
        }
        UserCouponResponse userCouponResponse = new UserCouponResponse();
        userCouponResponse.setPrice(couponByUidAndAndCourse.getPrice());
        userCouponResponse.setUid(couponByUidAndAndCourse.getUid());
        userCouponResponse.setCoursePrice(courseByCode.getPrice());


        return userCouponResponse;
    }

    @Override
    public SuccessResponse changeTheCouponStatus(String couponId) {

        authentication();
        if(couponId==null){
            throw new ErrorException("Invalid Coupon", VarList.RSP_NO_DATA_FOUND);
        }
        Coupon couponByUid = couponRepository.getCouponByUid(couponId);
        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setVariable("200");
        if(couponByUid.getIsActive()==1){
        couponByUid.setIsActive((byte)0);
            successResponse.setMessage("DEACTIVE");
        }else{
            couponByUid.setIsActive((byte)1);
            successResponse.setMessage("ACTIVE");
        }
        couponRepository.save(couponByUid);
        return successResponse;
    }
}
