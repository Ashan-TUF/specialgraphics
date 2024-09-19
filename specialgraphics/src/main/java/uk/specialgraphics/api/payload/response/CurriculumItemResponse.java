package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;
import uk.specialgraphics.api.entity.CurriculumItemType;

import java.util.List;

@Data
@ToString
public class CurriculumItemResponse {
    private String itemCode;
    private String description;
    private String title;
    private CurriculumItemType curriculumItemType;
    private List<CurriculumItemFileResponse> curriculumItemFiles;
    private  List<CurriculumItemZipFileResponse> curriculumItemZipFileResponses;
    private Boolean isQuizeAvailable;
    
}
