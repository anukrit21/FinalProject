package com.demoApp.mess.service;

import com.demoApp.mess.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${app.file-storage.upload-dir}")
    private String uploadDir;

    public String uploadSubscriptionImage(MultipartFile file) {
        return uploadFile(file, "subscription-images");
    }

    public String uploadFile(MultipartFile file, String subdirectory) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path targetLocation = Path.of(uploadDir, subdirectory, fileName);
            Files.createDirectories(targetLocation.getParent());
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return subdirectory + "/" + fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file. Please try again!", ex);
        }
    }

    public void deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return;
        }
        try {
            Path targetLocation = Path.of(uploadDir, filePath);
            Files.deleteIfExists(targetLocation);
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file. Please try again!", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path targetLocation = Path.of(uploadDir, "uploads", fileName); 
            Files.createDirectories(targetLocation.getParent());          
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return "uploads/" + fileName; 
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename(), ex);
        }
    }
    

    public String uploadUserImage(MultipartFile image) {
        return uploadFile(image, "user-images");
    }
    
}