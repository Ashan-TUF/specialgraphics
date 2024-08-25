package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;
import uk.specialgraphics.api.entity.Course;

import java.util.List;

@Data
@ToString
public class AllCourseResponse {
    private List<Course> courseList;
}
