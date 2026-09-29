package com.ordermymeal.catalogue.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ordermymeal.catalogue.model.Vendor;

public interface VendorRepository extends JpaRepository<Vendor, Long> {

    Optional<Vendor> findByVendorIdAndOrganizationId(
            Long vendorId,
            Long organizationId);

    List<Vendor> findByOrganizationIdOrderByVendorIdDesc(
            Long organizationId);
}   

