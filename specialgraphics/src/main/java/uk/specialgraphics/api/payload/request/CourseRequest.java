package uk.specialgraphics.api.payload.request;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
public class CourseRequest {
    private String code;
    private String title;
    private Double price;
    private MultipartFile img;
    private String description;
    private String promotionalVideo;
    private String points;
}
