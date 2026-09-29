package com.ordermymeal.catalogue.service;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ordermymeal.auth.model.Permission;
import com.ordermymeal.catalogue.dto.CatalogueItemResponse;
import com.ordermymeal.catalogue.dto.UpdateCatalogueItemRequest;
import com.ordermymeal.catalogue.model.CatalogueItem;
import com.ordermymeal.catalogue.repository.CatalogueItemRepository;

@Service
public class UpdateItemService {

    private final CatalogueItemRepository catalogueItemRepository;
    private final CatalogueContextService catalogueContextService;

    public UpdateItemService(
            CatalogueItemRepository catalogueItemRepository,
            CatalogueContextService catalogueContextService) {

        this.catalogueItemRepository = catalogueItemRepository;
        this.catalogueContextService = catalogueContextService;
    }

    @Transactional
    public CatalogueItemResponse updateItem(
            Long membershipId,
            Long catalogueItemId,
            UpdateCatalogueItemRequest request) {

        Long organizationId =
                catalogueContextService.requireOrganization(
                        membershipId,
                        "vendor.menu.manage");

        CatalogueItem item =
                catalogueItemRepository
                        .findByCatalogueItemIdAndOrganizationId(
                                catalogueItemId,
                                organizationId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Catalogue item not found."));

        String currency = request.currency();

        if (currency == null || currency.isBlank()) {
            currency = "INR";
        }

        currency = currency.trim().toUpperCase();

        if (!"INR".equals(currency)) {
            throw new IllegalArgumentException(
                    "Only INR is supported.");
        }

        String status = request.status().trim().toUpperCase();

        if (!status.equals("ACTIVE")
                && !status.equals("INACTIVE")) {

            throw new IllegalArgumentException(
                    "Catalogue item status must be ACTIVE or INACTIVE.");
        }

        item.setName(request.name().trim());
        item.setDescription(request.description());
        item.setCategory(request.category());
        item.setDietaryTag(request.dietaryTag());
        item.setPriceMinor(request.priceMinor());
        item.setCurrency(currency);
        item.setGstRateBp(request.gstRateBp());
        item.setStatus(status);
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

