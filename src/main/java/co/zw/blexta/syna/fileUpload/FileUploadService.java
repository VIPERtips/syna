package co.zw.blexta.syna.fileUpload;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${spring.file-upload.location}")
    private String uploadDir;

    @Value("${spring.file-upload.base-url}")
    private String baseUrl;

    private void createDirectoryIfNotExists() throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath, new FileAttribute<?>[0]);
        }
    }

    public String storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File is empty or missing");
        }

        createDirectoryIfNotExists();

        String originalFileName = file.getOriginalFilename();
        String extension = getFileExtension(originalFileName);
        if (extension.isEmpty()) {
            throw new IOException("Invalid file name or missing extension");
        }

        String newFileName = UUID.randomUUID().toString() + extension;
        Path targetLocation = Paths.get(uploadDir).resolve(newFileName);

        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);


        return baseUrl + newFileName;
    }

    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int extensionIdx = filename.lastIndexOf(".");
        return extensionIdx > 0 ? filename.substring(extensionIdx) : "";
    }
}
