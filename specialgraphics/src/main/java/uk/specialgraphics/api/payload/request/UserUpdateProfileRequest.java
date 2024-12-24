package uk.specialgraphics.api.payload.request;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
public class UserUpdateProfileRequest {
    private String fname;
    private String lname;
    private String email;
    private String mobile;
    private int country;
    private MultipartFile img;

}
