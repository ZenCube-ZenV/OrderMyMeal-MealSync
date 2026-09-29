package com.ordermymeal.catalogue.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "catalogue_items")
public class CatalogueItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "catalogue_item_id")
    private Long catalogueItemId;

    @Column(name = "org_id", nullable = false)
    private Long organizationId;

    @Column(name = "vendor_id", nullable = false)
    private Long vendorId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String category;

    @Column(name = "dietary_tag", length = 50)
    private String dietaryTag;

    @Column(name = "photo_object_key", length = 500)
    private String photoObjectKey;

    @Column(name = "price_minor", nullable = false)
    private Long priceMinor;

    @Column(nullable = false, length = 10)
    private String currency = "INR";

    @Column(name = "gst_rate_bp", nullable = false)
    private Integer gstRateBp = 0;

    @Column(nullable = false, length = 30)
    private String status = "ACTIVE";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public CatalogueItem() {
    }

    public Long getCatalogueItemId() {
        return catalogueItemId;
    }

    public void setCatalogueItemId(Long catalogueItemId) {
        this.catalogueItemId = catalogueItemId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDietaryTag() {
        return dietaryTag;
    }

    public void setDietaryTag(String dietaryTag) {
        this.dietaryTag = dietaryTag;
    }

    public String getPhotoObjectKey() {
        return photoObjectKey;
    }

    public void setPhotoObjectKey(String photoObjectKey) {
        this.photoObjectKey = photoObjectKey;
    }

    public Long getPriceMinor() {
        return priceMinor;
    }

    public void setPriceMinor(Long priceMinor) {
        this.priceMinor = priceMinor;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getGstRateBp() {
        return gstRateBp;
    }

    public void setGstRateBp(Integer gstRateBp) {
        this.gstRateBp = gstRateBp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}  

