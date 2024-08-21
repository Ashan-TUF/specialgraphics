package uk.specialgraphics.api.service;

import org.springframework.web.multipart.MultipartFile;
import uk.specialgraphics.api.payload.response.ApiResponse;

public interface VideoFileUploadService {
    ApiResponse saveFileChunk(MultipartFile file, String index, String totalChunks, String fileName);
}
