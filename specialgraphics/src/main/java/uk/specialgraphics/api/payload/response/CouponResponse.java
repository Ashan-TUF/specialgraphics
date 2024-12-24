package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CouponResponse {
    private String uid;
    private String courseName;
    private double price;
    boolean isActive;

}
