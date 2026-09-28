package com.ordermymeal.catalogue.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ordermymeal.catalogue.config.CatalogueProperties;

@Service
public class PhotoStorageService {

    private final CatalogueProperties catalogueProperties;

    public PhotoStorageService(
            CatalogueProperties catalogueProperties) {

        this.catalogueProperties = catalogueProperties;
    }

    public String store(
            Long catalogueItemId,
            MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Photo file is required.");
        }

        String contentType = file.getContentType();

        if (contentType == null
                || !contentType.startsWith("image/")) {

            throw new IllegalArgumentException(
                    "Only image files are allowed.");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException(
                    "Photo size must not exceed 5 MB.");
        }

        String originalFilename =
                file.getOriginalFilename();

        String extension = "";

        if (originalFilename != null
                && originalFilename.contains(".")) {

            extension =
                    originalFilename.substring(
                            originalFilename.lastIndexOf("."));
        }

        String fileName =
                UUID.randomUUID() + extension;

        Path directory =
                Path.of(
                        catalogueProperties
                                .getPhotoStorageDirectory(),
                        "catalogue-items",
                        String.valueOf(catalogueItemId));

        try {
            Files.createDirectories(directory);

            Path target =
                    directory.resolve(fileName);

            try (InputStream inputStream =
                         file.getInputStream()) {

                Files.copy(
                        inputStream,
                        target,
                        StandardCopyOption.REPLACE_EXISTING);
            }

            return target.toString();

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "Unable to store catalogue item photo.",
                    exception);
        }
    }
}  