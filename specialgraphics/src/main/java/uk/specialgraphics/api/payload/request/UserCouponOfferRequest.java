package uk.specialgraphics.api.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserCouponOfferRequest {
    private String courseCode;
    private String coupon;
}
