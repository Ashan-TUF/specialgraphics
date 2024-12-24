package uk.specialgraphics.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.specialgraphics.api.payload.request.AddCouponRequest;
import uk.specialgraphics.api.payload.request.AddPurchasedCoursesRequest;
import uk.specialgraphics.api.payload.request.UserCouponOfferRequest;
import uk.specialgraphics.api.payload.response.CouponResponse;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.payload.response.UserCouponResponse;
import uk.specialgraphics.api.service.CouponService;
import uk.specialgraphics.api.service.PurchaseService;

import java.util.List;

@RestController
@RequestMapping(value = "coupon")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CouponController {
    @Autowired
    PurchaseService purchaseService;
    @Autowired
    CouponService couponService;

    @PostMapping("/addCoupon")
    public SuccessResponse addToStudentsPurchasedCourses(AddCouponRequest addCouponRequest) {
        return couponService.addNewCoupon(addCouponRequest);
    }

    @PostMapping("/getAllCoupons")
    public List<CouponResponse> getAllCoupons() {
        return couponService.getAllCoupons();
    }

    @PostMapping("/getUserCourseOffer")
    public UserCouponResponse getUserOfferForCoupons(UserCouponOfferRequest userCouponOfferRequest) {
        return couponService.userGetOffer(userCouponOfferRequest);
    }

    @GetMapping("/changeCouponStatus/{cid}")
    public SuccessResponse getUserOfferForCoupons(@PathVariable String cid) {
        return couponService.changeTheCouponStatus(cid);
    }

}
