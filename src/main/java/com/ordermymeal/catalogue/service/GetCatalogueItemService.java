package com.ordermymeal.catalogue.service;

import com.ordermymeal.catalogue.dto.CatalogueItemResponse;
import com.ordermymeal.catalogue.model.CatalogueItem;
import com.ordermymeal.catalogue.repository.CatalogueItemRepository;
import org.springframework.stereotype.Service;

@Service
public class GetCatalogueItemService {

    private final CatalogueItemRepository catalogueItemRepository;

    public GetCatalogueItemService(
            CatalogueItemRepository catalogueItemRepository) {

        this.catalogueItemRepository = catalogueItemRepository;
    }

    public CatalogueItemResponse execute(
            Long catalogueItemId,
            Long organizationId) {

        CatalogueItem item = catalogueItemRepository
                .findByCatalogueItemIdAndOrganizationId(
                        catalogueItemId,
                        organizationId
                )
                .orElseThrow(() ->
                        new CatalogueNotFoundException(
                                "Catalogue item not found"
                        )
                );

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
                item.getStatus()
        );
    }
} 

