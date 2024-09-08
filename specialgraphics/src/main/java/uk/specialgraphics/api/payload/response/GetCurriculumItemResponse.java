package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;
import uk.specialgraphics.api.entity.CurriculumItemType;

import java.util.List;

@Data
@ToString
public class GetCurriculumItemResponse {
    private String description;
    private String title;
    private CurriculumItemType curriculumItemType;
}

