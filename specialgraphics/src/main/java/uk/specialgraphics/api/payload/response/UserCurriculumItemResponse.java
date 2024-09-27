package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;
import uk.specialgraphics.api.entity.CurriculumItemType;

import java.util.List;

@Data
@ToString
public class UserCurriculumItemResponse {
    private String itemCode;
    private String description;
    private String title;
    private CurriculumItemType curriculumItemType;
    private List<UserCurriculumItemFileResponse> curriculumItemFiles;
    private List<CurriculumItemZipFileResponse> CurriculumItemZipFileResponse;
    private Boolean isQuizeAvailable;
    private Boolean isQuizPerform;
    
}
