package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AnswerResponse {
    private String answer;
    private boolean istrue;
    private int id;

}
