package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;
import uk.specialgraphics.api.entity.CurriculumItemFileType;

@Data
@ToString
public class CurriculumItemFileResponse {
    private int id;
    private String title;
    private String url;
    private double videoLength;
    private CurriculumItemFileType curriculumItemFileType;
}
