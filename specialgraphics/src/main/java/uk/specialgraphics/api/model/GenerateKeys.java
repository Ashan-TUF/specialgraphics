package uk.specialgraphics.api.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GenerateKeys {

    private String privateKey;

    private String publicKey;

}
