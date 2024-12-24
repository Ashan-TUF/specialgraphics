package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ViewAllUseResponse {
    private String fname;
    private String lname;
    private String email;
//    private String country;
    private int status;
//    private String registred_date;
    private String mobile;
//    private int country_id;


}
