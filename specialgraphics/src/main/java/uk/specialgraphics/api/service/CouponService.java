package uk.specialgraphics.api.service;

import uk.specialgraphics.api.payload.request.AddCouponRequest;
import uk.specialgraphics.api.payload.request.AddPurchasedCoursesRequest;
import uk.specialgraphics.api.payload.request.UserCouponOfferRequest;
import uk.specialgraphics.api.payload.response.CouponResponse;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.payload.response.UserCouponResponse;

import java.util.List;

public interface CouponService {
    SuccessResponse addNewCoupon(AddCouponRequest addCouponRequest);

    List<CouponResponse> getAllCoupons();

    UserCouponResponse userGetOffer(UserCouponOfferRequest userCouponOfferRequest);

    SuccessResponse changeTheCouponStatus(String couponId);

}
