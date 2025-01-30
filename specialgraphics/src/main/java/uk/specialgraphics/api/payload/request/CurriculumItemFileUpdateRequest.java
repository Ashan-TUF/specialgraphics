package uk.specialgraphics.api.payload.request;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
public class CurriculumItemFileUpdateRequest {
    private String title;
    private String id;
    private MultipartFile zip;
}
