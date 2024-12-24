package uk.specialgraphics.api.payload.request;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class AddPurchasedCoursesRequest {
    private Integer paymentMethodId;
    private String courseCode;
    private Double totalPrice;
    private String details;
}
