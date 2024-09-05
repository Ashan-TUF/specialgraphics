package uk.specialgraphics.api.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UpdateQuizeItemRequest {
    private String questionItemCode;
    private String Question;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private int correctAnswer;
}
