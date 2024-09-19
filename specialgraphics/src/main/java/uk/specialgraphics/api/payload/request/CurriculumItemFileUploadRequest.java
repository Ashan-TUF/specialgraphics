package uk.specialgraphics.api.payload.request;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
public class CurriculumItemFileUploadRequest {
    private String title;
    private String curriculumCode;
    private MultipartFile zip;
}
