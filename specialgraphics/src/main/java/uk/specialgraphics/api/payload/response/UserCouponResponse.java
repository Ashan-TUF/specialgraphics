package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserCouponResponse {
    private String uid;
    private double price;
    private double coursePrice;

}
