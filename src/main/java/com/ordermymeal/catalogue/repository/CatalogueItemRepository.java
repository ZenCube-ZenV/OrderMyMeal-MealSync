package com.ordermymeal.catalogue.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ordermymeal.catalogue.model.CatalogueItem;

public interface CatalogueItemRepository
        extends JpaRepository<CatalogueItem, Long> {

    Optional<CatalogueItem> findByCatalogueItemIdAndOrganizationId(
            Long catalogueItemId,
            Long organizationId);

    List<CatalogueItem> findByOrganizationIdOrderByCatalogueItemIdDesc(
            Long organizationId);

    List<CatalogueItem> findByVendorIdAndOrganizationIdOrderByCatalogueItemIdDesc(
            Long vendorId,
            Long organizationId);
}  