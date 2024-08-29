package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class CourseSectionResponse {
    private String sectionCode;
    private String sectionName;
    private List<CurriculumItemResponse> curriculumItems;
}
