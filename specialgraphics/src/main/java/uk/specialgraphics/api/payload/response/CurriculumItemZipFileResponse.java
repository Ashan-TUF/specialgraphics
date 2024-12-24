package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;
import uk.specialgraphics.api.entity.CurriculumItemFileType;

@Data
@ToString
public class CurriculumItemZipFileResponse {
    private int id;
    private String title;
    private String url;
    private String date;
    private CurriculumItemFileType curriculumItemFileType;
    private boolean complete;
    private double marks;

}
