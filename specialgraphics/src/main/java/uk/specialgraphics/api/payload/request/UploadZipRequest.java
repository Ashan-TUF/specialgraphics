package uk.specialgraphics.api.payload.request;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
public class UploadZipRequest {
    private int zipId;
    private String courseCode;
    private String curriculumCode;
    private MultipartFile zip;
}
