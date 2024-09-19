package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class UserCourseSectionResponse {
    private String sectionCode;
    private String sectionName;
    private List<UserCurriculumItemResponse> curriculumItems;
}
