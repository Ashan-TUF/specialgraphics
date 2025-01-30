package uk.specialgraphics.api.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UpdateSectionCurriItemRequest {
    private String title;
    private String sectionCode;
    private String description;
    private String curriCode;
}
