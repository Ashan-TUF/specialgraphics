package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;
import uk.specialgraphics.api.entity.CourseSection;

import java.util.Date;
import java.util.List;

@Data
@ToString
public class CourseResponse {
    private String code;
    private String title;
    private String img;
    private String promotionalVideo;
    private Date createdDate;
    private Integer buyCount;
    private String description;
    private String points;
    private Double price;
    private List<CourseSectionResponse> courseSections;

    private String prefix;
    private String prefixColor;
    private String titleColor;
    private String keyHighlights;
    private String outcomes;
    private String sOutcomes;

}
