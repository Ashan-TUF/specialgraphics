package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

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
}
