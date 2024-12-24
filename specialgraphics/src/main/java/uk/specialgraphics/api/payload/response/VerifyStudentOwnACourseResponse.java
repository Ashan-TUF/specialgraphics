package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class VerifyStudentOwnACourseResponse {
    private double price;
    private boolean isVerify;

}
