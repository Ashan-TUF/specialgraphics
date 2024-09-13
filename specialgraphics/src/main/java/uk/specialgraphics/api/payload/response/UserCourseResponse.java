package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserCourseResponse {
    private String courseCode;
    private String title;
    private String image;
    private double progress;
    private String finalMessage;

}
