package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class QuizesInCurriculumItemResponse {
    private String curriculumItemCode;
    private List<QuestionAndAnswerResponse> answerResponses;

}
