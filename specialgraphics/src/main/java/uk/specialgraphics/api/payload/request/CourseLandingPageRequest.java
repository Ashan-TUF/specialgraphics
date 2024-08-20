package uk.specialgraphics.api.payload.request;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
public class CourseLandingPageRequest {
    private String courseCode;
    private String course_tile;
    private String course_subtitle;
    private String description;
    private int language;
    private int level;
    private MultipartFile course_image;
    private MultipartFile promotional_video;

}
