package uk.specialgraphics.api.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserLoginRequset {
    public String email;
    public String password;
    public boolean remember;
}
