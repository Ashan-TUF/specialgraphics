package uk.specialgraphics.api.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ForgotPasswordRequest {
    private String email;
    private String verification;
    private String password;
    private String repeatPassword;

}
