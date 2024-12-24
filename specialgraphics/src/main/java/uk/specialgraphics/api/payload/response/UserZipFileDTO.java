package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;
import uk.specialgraphics.api.entity.CurriculumItemFileType;

import java.time.LocalDateTime;

@Data
@ToString
public class UserZipFileDTO {
    private int marks;
    private String status;
    private LocalDateTime uploadDate;
    private String url;
    public UserZipFileDTO(int marks, String status, LocalDateTime uploadDate, String url) {
        this.marks = marks;
        this.status = status;
        this.uploadDate = uploadDate;
        this.url = url;
    }

}
