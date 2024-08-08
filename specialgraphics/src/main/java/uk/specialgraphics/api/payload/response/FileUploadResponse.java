package uk.specialgraphics.api.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FileUploadResponse {
    private String filename;
    private String Url;
}

