package uk.specialgraphics.api.payload.request;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
public class CourseRequest {
    private String course_title;
    private Double default_price;
    private MultipartFile img;
    private MultipartFile test_video;
}
