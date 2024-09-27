package uk.specialgraphics.api.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserSubmitMcqItemRequest {
    private String mcqItemCode;
    private int userAnswer;
}
