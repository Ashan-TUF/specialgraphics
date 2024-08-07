package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GeneralUserProfileResponse {
    private String code;
    private String message;
    private String token;
    private String variable;
}
