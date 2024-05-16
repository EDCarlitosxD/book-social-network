package com.booknetwork.booknetwork.files;

import com.booknetwork.booknetwork.book.domain.Book;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${application.file.upload.photos-output-path}")
    private String fileUploadPath;


    public String saveFile(
            @NonNull MultipartFile fileSource,
            @NonNull Book book,
            @NonNull Long userId) {
        final String fileUploadSubPath = "users" + File.separator + userId;
        return uploadFile(fileSource,fileUploadSubPath);
    }

    private String uploadFile(
            @NonNull MultipartFile fileSource,
            @NonNull String fileUploadSubPath
    ) {
        final String finalUploadPath = fileUploadPath + File.separator + fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);
        if (!targetFolder.exists()) {
            boolean folderCreated = targetFolder.mkdirs();
            if (!folderCreated) {
                log.warn("Failed to create folder");
                return null;
            }
        }
        final String fileExtension = getFileExtension(fileSource.getOriginalFilename());
        String targetFilePath = finalUploadPath + File.separator + System.currentTimeMillis() + fileExtension;
        Path targetPath = Paths.get(targetFilePath);

        try{
            Files.write(targetPath,fileSource.getBytes());
            log.info("Successfully uploaded file");
            return targetFilePath;
        }catch (IOException e){
            log.warn("Failed was not saved ",e);
        }
        return null;
    }

    private String getFileExtension(String fileName) {
        if(fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        if(lastDotIndex == -1) {
            return "";
        }

        return fileName.substring(lastDotIndex+1).toLowerCase();
    }
}
