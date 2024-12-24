package uk.specialgraphics.api.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddCouponRequest {
    private String courseCode;
    private double price;
}
