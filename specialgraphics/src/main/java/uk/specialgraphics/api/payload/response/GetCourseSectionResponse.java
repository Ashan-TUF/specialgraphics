package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GetCourseSectionResponse {
    private String sectionName;
    private List<GetCurriculumItemResponse> curriculumItems;
}
