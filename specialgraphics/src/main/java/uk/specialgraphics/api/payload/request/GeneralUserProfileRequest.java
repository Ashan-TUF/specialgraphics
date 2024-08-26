package uk.specialgraphics.api.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GeneralUserProfileRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String mobile;
    private Integer country;

}
