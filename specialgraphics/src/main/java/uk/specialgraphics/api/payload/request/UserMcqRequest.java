package uk.specialgraphics.api.payload.request;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class UserMcqRequest {
    private String curriculumItemCode;
    List<UserSubmitMcqItemRequest> userSubmitMcqItemRequest;
}
