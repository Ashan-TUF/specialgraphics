package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class UserQuizesInCurriculumItemResponse {
    private String curriculumItemCode;
    private List<UserQuestionAndAnswerResponse> answerResponses;

}
