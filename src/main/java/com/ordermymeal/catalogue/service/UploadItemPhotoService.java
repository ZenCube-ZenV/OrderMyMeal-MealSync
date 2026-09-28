package com.ordermymeal.catalogue.service;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ordermymeal.auth.model.Permission;
import com.ordermymeal.catalogue.dto.CatalogueItemResponse;
import com.ordermymeal.catalogue.model.CatalogueItem;
import com.ordermymeal.catalogue.repository.CatalogueItemRepository;

@Service
public class UploadItemPhotoService {

    private final CatalogueItemRepository catalogueItemRepository;
    private final CatalogueContextService catalogueContextService;
    private final PhotoStorageService photoStorageService;

    public UploadItemPhotoService(
            CatalogueItemRepository catalogueItemRepository,
            CatalogueContextService catalogueContextService,
            PhotoStorageService photoStorageService) {

        this.catalogueItemRepository =
                catalogueItemRepository;

        this.catalogueContextService =
                catalogueContextService;

        this.photoStorageService =
                photoStorageService;
    }

    @Transactional
    public CatalogueItemResponse uploadItemPhoto(
            Long membershipId,
            Long catalogueItemId,
            MultipartFile file) {

        Long organizationId =
                catalogueContextService.requireOrganization(
                        membershipId,
                        Permission.VENDOR_MENU_MANAGE);

        CatalogueItem item =
                catalogueItemRepository
                        .findByCatalogueItemIdAndOrganizationId(
                                catalogueItemId,
                                organizationId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Catalogue item not found."));

        String objectKey =
                photoStorageService.store(
                        catalogueItemId,
                        file);

        item.setPhotoObjectKey(objectKey);
        item.setUpdatedAt(Instant.now());

        CatalogueItem savedItem =
                catalogueItemRepository.save(item);

        return toResponse(savedItem);
    }

    private CatalogueItemResponse toResponse(
            CatalogueItem item) {

        return new CatalogueItemResponse(
                item.getCatalogueItemId(),
                item.getOrganizationId(),
                item.getVendorId(),
                item.getName(),
                item.getDescription(),
                item.getCategory(),
                item.getDietaryTag(),
                item.getPhotoObjectKey(),
                item.getPriceMinor(),
                item.getCurrency(),
                item.getGstRateBp(),
                item.getStatus());
    }
}  