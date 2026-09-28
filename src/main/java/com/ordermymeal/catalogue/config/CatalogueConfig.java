package com.ordermymeal.catalogue.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CatalogueProperties.class)
public class CatalogueConfig {
}  