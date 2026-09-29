package com.ordermymeal.catalogue.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ordermymeal.catalogue.dto.CatalogueItemResponse;
import com.ordermymeal.catalogue.model.CatalogueItem;
import com.ordermymeal.catalogue.repository.CatalogueItemRepository;

@Service
public class GetCatalogueItemsService {

    private final CatalogueItemRepository catalogueItemRepository;

    public GetCatalogueItemsService(
            CatalogueItemRepository catalogueItemRepository) {

        this.catalogueItemRepository = catalogueItemRepository;
    }

    public List<CatalogueItemResponse> execute(
            Long organizationId,
            Long vendorId) {

        List<CatalogueItem> items;

        if (vendorId != null) {

            items = catalogueItemRepository
                    .findByVendorIdAndOrganizationIdOrderByCatalogueItemIdDesc(
                            vendorId,
                            organizationId
                    );

        } else {

            items = catalogueItemRepository
                    .findByOrganizationIdOrderByCatalogueItemIdDesc(
                            organizationId
                    );
        }

        return items.stream()
                .map(this::toResponse)
                .toList();
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
                item.getStatus()
        );
    }
}  

