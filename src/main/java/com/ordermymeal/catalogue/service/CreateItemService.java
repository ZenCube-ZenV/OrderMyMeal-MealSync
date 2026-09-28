package com.ordermymeal.catalogue.service;

import com.ordermymeal.auth.model.Permission;
import com.ordermymeal.catalogue.dto.CatalogueItemResponse;
import com.ordermymeal.catalogue.dto.CreateCatalogueItemRequest;
import com.ordermymeal.catalogue.model.CatalogueItem;
import com.ordermymeal.catalogue.model.Vendor;
import com.ordermymeal.catalogue.repository.CatalogueItemRepository;
import com.ordermymeal.catalogue.repository.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class CreateItemService {

    private final CatalogueItemRepository catalogueItemRepository;
    private final VendorRepository vendorRepository;
    private final CatalogueContextService catalogueContextService;

    public CreateItemService(
            CatalogueItemRepository catalogueItemRepository,
            VendorRepository vendorRepository,
            CatalogueContextService catalogueContextService) {

        this.catalogueItemRepository = catalogueItemRepository;
        this.vendorRepository = vendorRepository;
        this.catalogueContextService = catalogueContextService;
    }

    @Transactional
    public CatalogueItemResponse createItem(
            Long membershipId,
            CreateCatalogueItemRequest request) {

        Long organizationId =
                catalogueContextService.requireOrganization(
                        membershipId,
                        Permission.VENDOR_MENU_MANAGE);

        Vendor vendor =
                vendorRepository
                        .findByVendorIdAndOrganizationId(
                                request.vendorId(),
                                organizationId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Vendor not found."));

        if (!"ACTIVE".equalsIgnoreCase(vendor.getStatus())) {
            throw new IllegalArgumentException(
                    "Catalogue item cannot be created for an inactive vendor.");
        }

        String currency = request.currency();

        if (currency == null || currency.isBlank()) {
            currency = "INR";
        }

        currency = currency.trim().toUpperCase();

        if (!"INR".equals(currency)) {
            throw new IllegalArgumentException(
                    "Only INR is supported.");
        }

        Instant now = Instant.now();

        CatalogueItem item = new CatalogueItem();

        item.setOrganizationId(organizationId);
        item.setVendorId(vendor.getVendorId());
        item.setName(request.name().trim());
        item.setDescription(request.description());
        item.setCategory(request.category());
        item.setDietaryTag(request.dietaryTag());
        item.setPriceMinor(request.priceMinor());
        item.setCurrency(currency);
        item.setGstRateBp(request.gstRateBp());
        item.setStatus("ACTIVE");
        item.setCreatedAt(now);
        item.setUpdatedAt(now);

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