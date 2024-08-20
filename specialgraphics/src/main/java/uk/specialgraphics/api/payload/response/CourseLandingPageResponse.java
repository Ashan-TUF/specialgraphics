package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CourseLandingPageResponse {
    private String courseTitle;
    private String courseSubTitle;
    private String description;
    private String languageId;
    private String language;
    private String levelId;
    private String level;
    private String courseImage;
    private String PromotionalVideo;
    private double courseLength;
}
