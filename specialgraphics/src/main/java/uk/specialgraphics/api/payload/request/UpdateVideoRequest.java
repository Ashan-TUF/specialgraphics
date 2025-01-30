package uk.specialgraphics.api.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UpdateVideoRequest {
    private String courseCode;
    private String curriculumItemCode;
    private int id;
    private String generatedVideoName;
    private Double videoLength;
    private String originalVideoName;
}
