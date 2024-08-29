package uk.specialgraphics.api.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.specialgraphics.api.config.Config;
import uk.specialgraphics.api.payload.response.ApiResponse;
import uk.specialgraphics.api.service.VideoFileUploadService;

import java.io.*;


@Service
public class VideoFileUploadServiceImpl implements VideoFileUploadService {
    @Override
    public ApiResponse saveFileChunk(MultipartFile fileObj, String index, String totalChunks, String fileName) {
        boolean status = false;
        String msg = "";
        File directory = new File(Config.UPLOAD_URL + Config.VIDEOS_UPLOAD_URL);

        if (!directory.exists()) {
            directory.mkdirs();
        }


//        String[] parts = fileName.split("\\.");
//
//        String extension = parts[1];
//
//        String randomFilename = new Date().getTime() + "_" + UUID.randomUUID().toString().concat(".")
//                .concat(Objects.requireNonNull(extension));

        try {

            String folderName = fileName.replace('.', 'V');

            // Create folder if it doesn't exist
            File f1 = new File(Config.UPLOAD_URL + Config.VIDEOS_UPLOAD_URL + folderName);
            if (!f1.exists()) {
                f1.mkdir();
            }

            File file = new File(Config.UPLOAD_URL + Config.VIDEOS_UPLOAD_URL + folderName + "/" + index);

//            System.out.println(index + "/" + totalChunks + " " + fileName + " " + file1.getSize());

            // Transfer data from the input stream to the file output stream
            try (InputStream is = fileObj.getInputStream();
                 BufferedInputStream bis = new BufferedInputStream(is);
                 FileOutputStream fos = new FileOutputStream(file)) {

                byte[] buffer = new byte[8192]; // You can adjust the buffer size according to your needs
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace(); // Handle or log the exception appropriately
                status = false;
                msg = "Failed to process the file.";
            }

            // Delete temporary files and append if necessary
            if (f1.listFiles().length == Integer.parseInt(totalChunks)) {
                System.out.println("Appending...." + f1.listFiles().length);
                File finalFile = new File(Config.UPLOAD_URL + Config.VIDEOS_UPLOAD_URL + fileName);

                try (FileOutputStream fos2 = new FileOutputStream(finalFile)) {
                    for (int i = 1; i <= Integer.parseInt(totalChunks); i++) {
                        File chunk = new File(Config.UPLOAD_URL + Config.VIDEOS_UPLOAD_URL + folderName + "/" + i);
                        try (InputStream is2 = new FileInputStream(chunk);
                             BufferedInputStream bis2 = new BufferedInputStream(is2)) {

                            byte[] buffer = new byte[8192];
                            int bytesRead;
                            while ((bytesRead = bis2.read(buffer)) != -1) {
                                fos2.write(buffer, 0, bytesRead);
                            }
                        }
                        if (!chunk.delete()) {
                            System.out.println("Failed to delete chunk: " + chunk.getAbsolutePath());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace(); // Handle or log the exception appropriately
                    status = false;
                    msg = "Failed to process the file chunks.";
                }

                if (!f1.delete()) {
                    System.out.println("Failed to delete folder: " + f1.getAbsolutePath());
                }
            }

        } catch (Exception e) {
            status = false;
            msg = e.getMessage();
            e.printStackTrace();
        }

        return new ApiResponse(status, msg);
    }
}
