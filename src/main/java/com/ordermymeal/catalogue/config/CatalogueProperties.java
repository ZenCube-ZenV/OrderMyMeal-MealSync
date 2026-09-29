package com.ordermymeal.catalogue.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "catalogue")
public class CatalogueProperties {

    private String photoStorageDirectory = "uploads/catalogue";

    public String getPhotoStorageDirectory() {
        return photoStorageDirectory;
    }

    public void setPhotoStorageDirectory(
            String photoStorageDirectory) {

        this.photoStorageDirectory =
                photoStorageDirectory;
    }
}  

