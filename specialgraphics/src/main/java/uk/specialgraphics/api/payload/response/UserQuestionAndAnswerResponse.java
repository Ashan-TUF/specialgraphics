package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class UserQuestionAndAnswerResponse {
    private String questionItemCode;
    private String question;
    private List<AnswerResponse> answerResponses;


}
