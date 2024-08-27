package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddSectionCurriculumItemResponse {
    private String message;
    private String statusCode;
    private String sectionItemCode;
}
