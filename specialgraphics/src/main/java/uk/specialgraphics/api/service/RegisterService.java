package uk.specialgraphics.api.service;

import uk.specialgraphics.api.payload.request.ForgotPasswordRequest;
import uk.specialgraphics.api.payload.request.GeneralUserProfileRequest;
import uk.specialgraphics.api.payload.response.GeneralUserProfileResponse;
import uk.specialgraphics.api.payload.response.SuccessResponse;

public interface RegisterService {

    GeneralUserProfileResponse saveUser(GeneralUserProfileRequest generalUserProfileRequest);

    SuccessResponse verifyUser(String email,String verificationCode);
    SuccessResponse sendVerificatioCode(String email);
SuccessResponse changeTheUserPassword(ForgotPasswordRequest forgotPasswordRequest);

}
