package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ViewAllAdminNotMarkedCompleteCourseResponse {
    private String fname;
    private String lname;
    private String email;
    private String course;
    private int course_id;

}
