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

    public static FileUploadResponse saveFile(MultipartFile file, String type) throws IOException {
        try {
            String randomFilename = new Date().getTime() + "_" + UUID.randomUUID().toString().concat(".")
                    .concat(Objects.requireNonNull(FilenameUtils.getExtension(file.getOriginalFilename())));
            String path;
            if (type == "course-image") {
                path = Config.COURSE_IMAGES_UPLOAD_URL;
            } else {
                path = Config.OTHERS_UPLOAD_URL;
            }
            Path savePath = Paths.get(Config.UPLOAD_URL + path, randomFilename);
            Files.write(savePath, file.getBytes());

            FileUploadResponse fileUploadResponse = new FileUploadResponse();
            fileUploadResponse.setFilename(file.getOriginalFilename());
            fileUploadResponse.setUrl(path + randomFilename);
            return fileUploadResponse;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload the file", e);
        }
    }
}
