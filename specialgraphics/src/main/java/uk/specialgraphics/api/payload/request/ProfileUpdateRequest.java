package uk.specialgraphics.api.payload.request;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
public class ProfileUpdateRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String mobile;
    private Integer country;
    private MultipartFile profileImage;
}
