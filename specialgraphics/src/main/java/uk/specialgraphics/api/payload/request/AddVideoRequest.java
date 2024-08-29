package uk.specialgraphics.api.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddVideoRequest {
    private String courseCode;
    private String curriculumItemCode;
    private String generatedVideoName;
    private Double videoLength;
    private String originalVideoName;
}
