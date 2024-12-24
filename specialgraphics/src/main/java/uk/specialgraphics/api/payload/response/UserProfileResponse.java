package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString
public class UserProfileResponse {
    private String fname;
    private String lname;
    private String email;
    private String country;
    private boolean status;
    private String registred_date;
    private String mobile;
    private int country_id;
    private String img;

}
