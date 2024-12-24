package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class UserCourseViewResponse {
    private String code;
    private String title;
    private String img;
    private String description;
    private String points;
    private String nextLesson;
    private List<UserCourseSectionResponse> courseSections;
}
