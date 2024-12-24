package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;
import uk.specialgraphics.api.entity.CurriculumItemFileType;

@Data
@ToString
public class UserAssignmentZipFileResponseAdmin {
    private int id;
    private String title;
    private String section;
    private String course;
    private String fname;
    private String lname;
    private String email;
    private String date;
    private String url;
    private CurriculumItemFileType curriculumItemFileType;
    private boolean complete;
    private double marks;
   
}
