package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;
import uk.specialgraphics.api.entity.Country;

import java.util.List;

@Data
@ToString
public class AllCountryResponse {
    private List<Country> countries;
    private String variable;
}
