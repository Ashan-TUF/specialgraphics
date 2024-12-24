package uk.specialgraphics.api.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ChangePasswordRequest {
    private String oldPassword;
    private String repeatPassword;
    private String newPassword;
}
