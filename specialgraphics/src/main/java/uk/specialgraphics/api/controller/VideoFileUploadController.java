package uk.specialgraphics.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.specialgraphics.api.payload.response.ApiResponse;
import uk.specialgraphics.api.service.VideoFileUploadService;

import javax.servlet.annotation.MultipartConfig;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,  // 1 MB
        maxFileSize = 1024 * 1024 * 20,    // 20 MB
        maxRequestSize = 1024 * 1024 * 20  // 20 MB
)
public class VideoFileUploadController {

    @Autowired
    private VideoFileUploadService videoFileUploadService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadFileChunk(
            @RequestParam("index") String index,
            @RequestParam("totalChunks") String totalChunks,
            @RequestParam("fileName") String fileName,
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileSize") String fileSize) {

        System.out.println("index: "+index);

        try {
            ApiResponse response = videoFileUploadService.saveFileChunk(file, index, totalChunks, fileName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return null;
        }
    }
}
