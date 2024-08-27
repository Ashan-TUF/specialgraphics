package uk.specialgraphics.api.payload.request;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
public class AddSectionCurriculumItemRequest {
    private String courseCode;
    private String courseSectionCode;
    private String description;
    private String title;
}
