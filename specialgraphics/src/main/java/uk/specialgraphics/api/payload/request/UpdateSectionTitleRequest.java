package uk.specialgraphics.api.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UpdateSectionTitleRequest {
    private String title;
    private String sectionCode;
}
