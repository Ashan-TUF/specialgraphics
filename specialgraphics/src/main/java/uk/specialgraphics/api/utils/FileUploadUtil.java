package uk.specialgraphics.api.utils;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;
import uk.specialgraphics.api.config.Config;
import uk.specialgraphics.api.payload.response.FileUploadResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class FileUploadUtil {

    public static FileUploadResponse saveFile(MultipartFile file) throws IOException {
        try {
            String randomFilename = new Date().getTime() + "_" + UUID.randomUUID().toString().concat(".")
                    .concat(Objects.requireNonNull(FilenameUtils.getExtension(file.getOriginalFilename())));
            Path image1SavePath = Paths.get(Config.UPLOAD_URL, randomFilename);
            Files.write(image1SavePath, file.getBytes());

            FileUploadResponse fileUploadResponse = new FileUploadResponse();
            fileUploadResponse.setFilename(randomFilename);
            fileUploadResponse.setUrl(file.getOriginalFilename());
            return fileUploadResponse;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload the file", e);
        }
    }

    public static FileUploadResponse deleteFile(String filename) {
        String targetPath = Config.UPLOAD_URL + filename;
        try {
            Path filePath = Paths.get(targetPath);
            if (Files.exists(filePath)) {
                Files.delete(filePath);

                FileUploadResponse fileUploadResponse = new FileUploadResponse();
                fileUploadResponse.setFilename(filename);
                fileUploadResponse.setUrl(null);
                return fileUploadResponse;
            } else {
                throw new RuntimeException("File not found: " + filename);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete the file: " + filename, e);
        }
    }
}
