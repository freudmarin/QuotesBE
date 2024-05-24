package com.marin.quotesdashboardbackend.services;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file) {
        try {
            Path copyLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(copyLocation)) {
                Files.createDirectories(copyLocation);
            }

            // Store the file
            Path targetLocation = copyLocation.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Return the relative path for serving the file
            return "/images/" + file.getOriginalFilename();

        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename(), e);
        }
    }
}
