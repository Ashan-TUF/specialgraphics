package uk.specialgraphics.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import uk.specialgraphics.api.config.PasswordEncoderConfig;
import uk.specialgraphics.api.entity.Country;
import uk.specialgraphics.api.entity.GeneralUserProfile;
import uk.specialgraphics.api.entity.GupType;
import uk.specialgraphics.api.exception.ErrorException;
import uk.specialgraphics.api.payload.request.ForgotPasswordRequest;
import uk.specialgraphics.api.payload.request.GeneralUserProfileRequest;
import uk.specialgraphics.api.payload.response.GeneralUserProfileResponse;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.repository.CountryRepository;
import uk.specialgraphics.api.repository.GeneralUserProfileRepository;
import uk.specialgraphics.api.repository.GupTypeRepository;
import uk.specialgraphics.api.security.JwtTokenUtil;
import uk.specialgraphics.api.security.JwtUserDetailsServicePassword;
import uk.specialgraphics.api.service.EmailService;
import uk.specialgraphics.api.service.RegisterService;
import uk.specialgraphics.api.utils.VarList;

import java.util.Date;
import java.util.UUID;

@Service
public class RegisterServiceImpl implements RegisterService {
    private GeneralUserProfileRepository generalUserProfileRepository;
    private GupTypeRepository gupTypeRepository;
    private JwtUserDetailsServicePassword userDetailsServicePassword;
    private JwtTokenUtil jwtTokenUtil;
    private CountryRepository countryRepository;
    private EmailService emailService;

    @Autowired
    public RegisterServiceImpl(GeneralUserProfileRepository generalUserProfileRepository,
                               GupTypeRepository gupTypeRepository,
                               JwtUserDetailsServicePassword userDetailsServicePassword,
                               JwtTokenUtil jwtTokenUtil,
                               CountryRepository countryRepository,
                               EmailService emailService) {
        this.generalUserProfileRepository = generalUserProfileRepository;
        this.gupTypeRepository = gupTypeRepository;
        this.userDetailsServicePassword = userDetailsServicePassword;
        this.jwtTokenUtil = jwtTokenUtil;
        this.countryRepository = countryRepository;
        this.emailService=emailService;
    }

    @Override
    public GeneralUserProfileResponse saveUser(GeneralUserProfileRequest generalUserProfileRequest) {
        final String email = generalUserProfileRequest.getEmail();
        final String firstName = generalUserProfileRequest.getFirstName();
        final String lastName = generalUserProfileRequest.getLastName();
        final String password = generalUserProfileRequest.getPassword();
        final String mobile = generalUserProfileRequest.getMobile();
        final Integer country = generalUserProfileRequest.getCountry();

        if (email == null || email.isEmpty() ||
                firstName == null || firstName.isEmpty() ||
                lastName == null || lastName.isEmpty() || password == null || password.isEmpty() ||
                mobile == null || mobile.isEmpty() ||
                country == null || country.toString().isEmpty() || country == 0) {
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
        }

        GeneralUserProfile generalUserProfile = generalUserProfileRepository.getGeneralUserProfileByEmail(generalUserProfileRequest.getEmail());

        if (generalUserProfile == null) {

            generalUserProfile = new GeneralUserProfile();
            generalUserProfile.setEmail(email);
            generalUserProfile.setUserCode(UUID.randomUUID().toString());
            generalUserProfile.setVerificationCode(UUID.randomUUID().toString());
            generalUserProfile.setFirstName(firstName);
            generalUserProfile.setLastName(lastName);
            generalUserProfile.setMobile(mobile);
            Country countryFromDB = countryRepository.getCountryById(country);
            if (countryFromDB == null)
                throw new ErrorException("The country cannot be found in the database", VarList.RSP_NO_DATA_FOUND);
            generalUserProfile.setCountry(countryFromDB);
            PasswordEncoderConfig by = new PasswordEncoderConfig();
            String encryptedPwd = by.passwordEncoder().encode(generalUserProfileRequest.getPassword());
            generalUserProfile.setPassword(encryptedPwd);
            generalUserProfile.setRegisteredDate(new Date());
            generalUserProfile.setIsActive((byte) 0);
            GupType gupTypeObj = gupTypeRepository.getGupTypeById(2);
            if (gupTypeObj == null)
                throw new ErrorException("Invalid gup type id", VarList.RSP_NO_DATA_FOUND);
            generalUserProfile.setGupType(gupTypeObj);


            generalUserProfileRepository.save(generalUserProfile);

            UserDetails userDetails = userDetailsServicePassword.loadUserByUsername(generalUserProfile.getEmail());
            String token;
            try {
                token = jwtTokenUtil.generateToken(userDetails);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            GeneralUserProfileResponse generalUserProfileResponse = new GeneralUserProfileResponse();

            generalUserProfileResponse.setCode(generalUserProfile.getUserCode());
            generalUserProfileResponse.setToken(token);

            String emailContent = "<!DOCTYPE html>" +
                    "<html lang=\"en\">" +
                    "<head>" +
                    "    <meta charset=\"UTF-8\">" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                    "    <title>Verify Your Account</title>" +
                    "    <style>" +
                    "        body {" +
                    "            font-family: Arial, sans-serif;" +
                    "            margin: 0;" +
                    "            padding: 0;" +
                    "            background-color: #fff;" +
                    "            color: #000;" +
                    "            display: flex;" +
                    "            align-items: center;" +
                    "            justify-content: center;" +
                    "            height: 100vh;" +
                    "        }" +
                    "        .container {" +
                    "            text-align: center;" +
                    "            padding: 20px;" +
                    "            border: 1px solid #000;" +
                    "            width: 90%;" +
                    "            max-width: 500px;" +
                    "            background-color: #fff;" +
                    "        }" +
                    "        .logo {" +
                    "            width: 100px;" +
                    "            margin-bottom: 20px;" +
                    "        }" +
                    "        .verify-button {" +
                    "            background-color: #000;" +
                    "            color: #fff;" +
                    "            text-decoration: none;" +
                    "            padding: 10px 20px;" +
                    "            font-size: 16px;" +
                    "            border-radius: 5px;" +
                    "            margin-top: 20px;" +
                    "            display: inline-block;" +
                    "        }" +
                    "        .verify-button:hover {" +
                    "            background-color: #333;" +
                    "        }" +
                    "        .footer {" +
                    "            font-size: 12px;" +
                    "            margin-top: 20px;" +
                    "            color: #666;" +
                    "        }" +
                    "        .footer a {" +
                    "            color: #000;" +
                    "            text-decoration: none;" +
                    "        }" +
                    "    </style>" +
                    "</head>" +
                    "<body>" +
                    "    <div class=\"container\">" +
                    "        <!-- Logo -->" +
                    "        <img src=\"logo-url-here\" alt=\"Company Logo\" class=\"logo\">" +
                    "" +
                    "        <!-- Verification Message -->" +
                    "        <h1>Verify Your Account</h1>" +
                    "" +
                    "        <!-- Description -->" +
                    "        <p>Thank you for registering with us! To complete your account setup, please verify your email by clicking the button below.</p>" +
                    "" +
                    "        <!-- Verify Button -->" +
                    "        <a href=\"http://localhost/specialClass/verify?verificationCode=" + generalUserProfile.getVerificationCode() +
                    "&email=" + generalUserProfile.getEmail() + "\" class=\"verify-button\">Verify Your Account</a>" +
                    "" +
                    "        <!-- Footer -->" +
                    "        <div class=\"footer\">" +
                    "            <p>If you did not sign up for this account, please ignore this email.</p>" +
                    "        </div>" +
                    "    </div>" +
                    "</body>" +
                    "</html>";

            emailService.sendSimpleEmail(email,"Verify Your Special Graphics Class Account",
                    emailContent);

            generalUserProfileResponse.setMessage("Registered Success");
            generalUserProfileResponse.setVariable(VarList.RSP_SUCCESS);
            return generalUserProfileResponse;

        } else {
            throw new ErrorException("Email is already Registered", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse verifyUser(String email, String verificationCode) {
        if(email==null||email.isEmpty()||verificationCode==null||verificationCode.isEmpty()){
            throw new ErrorException("Invalid Request", VarList.RSP_NO_DATA_FOUND);
        }
        GeneralUserProfile generalUserProfileByEmail = generalUserProfileRepository.getGeneralUserProfileByEmail(email);
        if(generalUserProfileByEmail==null){
            throw new ErrorException("Invalid User", VarList.RSP_NO_DATA_FOUND);
        }
        if(!verificationCode.equals(generalUserProfileByEmail.getVerificationCode())){
            throw new ErrorException("Invalid verification", VarList.RSP_NO_DATA_FOUND);
        }
        generalUserProfileByEmail.setIsActive((byte)1);
        generalUserProfileByEmail.setVerificationCode(UUID.randomUUID().toString());

        generalUserProfileRepository.save(generalUserProfileByEmail);
        SuccessResponse successResponse=new SuccessResponse();
        successResponse.setVariable("200");
        successResponse.setMessage("Verification Success");
        return successResponse;
    }

    @Override
    public SuccessResponse sendVerificatioCode(String email) {
        if(email==null||email.isEmpty()){
            throw new ErrorException("Invalid Request", VarList.RSP_NO_DATA_FOUND);
        }
        GeneralUserProfile generalUserProfileByEmail = generalUserProfileRepository.getGeneralUserProfileByEmail(email);
        if(generalUserProfileByEmail==null){
            throw new ErrorException("Invalid User", VarList.RSP_NO_DATA_FOUND);
        }
        if(generalUserProfileByEmail.getIsActive()==(byte)0){
            throw new ErrorException("Your Account Is DEACTIVATE. Contact the Management ", VarList.RSP_NO_DATA_FOUND);
        }


        String emailContent = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Reset Your Password</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            background-color: #f9f9f9;\n" +
                "            color: #000;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "            height: 100vh;\n" +
                "        }\n" +
                "\n" +
                "        .container {\n" +
                "            text-align: center;\n" +
                "            padding: 20px;\n" +
                "            border: 1px solid #ddd;\n" +
                "            width: 90%;\n" +
                "            max-width: 500px;\n" +
                "            background-color: #fff;\n" +
                "            border-radius: 10px;\n" +
                "            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);\n" +
                "        }\n" +
                "\n" +
                "        .logo {\n" +
                "            width: 100px;\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .reset-button {\n" +
                "            background-color: #131516;\n" +
                "            color: #fff;\n" +
                "            text-decoration: none;\n" +
                "            padding: 10px 20px;\n" +
                "            font-size: 16px;\n" +
                "            border-radius: 5px;\n" +
                "            margin-top: 20px;\n" +
                "            display: inline-block;\n" +
                "        }\n" +
                "\n" +
                "        .reset-button:hover {\n" +
                "            background-color: #0056b3;\n" +
                "        }\n" +
                "\n" +
                "        .footer {\n" +
                "            font-size: 12px;\n" +
                "            margin-top: 20px;\n" +
                "            color: #666;\n" +
                "        }\n" +
                "\n" +
                "        .footer a {\n" +
                "            color: #007bff;\n" +
                "            text-decoration: none;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <!-- Logo -->\n" +
                "        <img src=\"logo-url-here\" alt=\"Company Logo\" class=\"logo\">\n" +
                "\n" +
                "        <!-- Password Reset Message -->\n" +
                "        <h1>Reset Your Password</h1>\n" +
                "\n" +
                "        <!-- Description -->\n" +
                "        <p>We received a request to reset your password. To proceed, Copy the Verification Code below to verify your request. <br> Place the Verification Code into Verification Code Field.</p>\n" +
                "\n" +
                "        <!-- Reset Button -->\n" +
                "        <label class=\"reset-button\">"+generalUserProfileByEmail.getVerificationCode()+"</label>\n" +
                "\n" +
                "            <!-- Footer -->\n" +
                "            <div class=\"footer\">\n" +
                "                <p>If you did not request a password reset, please ignore this email or contact support.</p>\n" +
                "\n" +
                "            </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "\n" +
                "</html>";

        emailService.sendSimpleEmail(email,"Special Graphics Classes Forgot Password Verification Code",
                emailContent);


        SuccessResponse successResponse=new SuccessResponse();
        successResponse.setVariable("200");
        successResponse.setMessage("Verification code Sent To email");
        return successResponse;
    }

    @Override
    public SuccessResponse changeTheUserPassword(ForgotPasswordRequest forgotPasswordRequest) {

        final String email= forgotPasswordRequest.getEmail();
        final String verification= forgotPasswordRequest.getVerification();
        final String newPassword= forgotPasswordRequest.getPassword();
        final String repeatPassword= forgotPasswordRequest.getRepeatPassword();

        if(email==null||email.isEmpty()||verification==null||verification.isEmpty()||newPassword==null||newPassword.isEmpty()||repeatPassword==null||repeatPassword.isEmpty()){
            throw new ErrorException("Invalid Request", VarList.RSP_NO_DATA_FOUND);
        }
        GeneralUserProfile generalUserProfileByEmail = generalUserProfileRepository.getGeneralUserProfileByEmail(email);
        if(generalUserProfileByEmail==null){
            throw new ErrorException("Invalid Email", VarList.RSP_NO_DATA_FOUND);
        }
        if(!newPassword.equals(repeatPassword)){
            throw new ErrorException("Repeat The Password Correctly", VarList.RSP_NO_DATA_FOUND);
        }
        if(!generalUserProfileByEmail.getVerificationCode().equals(verification)){
            throw new ErrorException("Invalid Verification Code", VarList.RSP_NO_DATA_FOUND);
        }
        PasswordEncoderConfig by = new PasswordEncoderConfig();
        String encryptedPwd = by.passwordEncoder().encode(newPassword);
        generalUserProfileByEmail.setPassword(encryptedPwd);
        generalUserProfileByEmail.setVerificationCode(UUID.randomUUID().toString());
        generalUserProfileRepository.save(generalUserProfileByEmail);

        SuccessResponse successResponse=new SuccessResponse();
        successResponse.setVariable("200");
        successResponse.setMessage("Password Change Success");
        return successResponse;
    }


}
