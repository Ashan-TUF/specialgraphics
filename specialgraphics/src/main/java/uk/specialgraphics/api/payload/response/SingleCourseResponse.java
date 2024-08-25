package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;
import uk.specialgraphics.api.entity.Course;

@Data
@ToString
public class SingleCourseResponse {

    private Course course;
}
