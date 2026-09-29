package com.ordermymeal.catalogue.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ordermymeal.auth.service.CurrentSessionService;
import com.ordermymeal.catalogue.dto.CatalogueItemResponse;
import com.ordermymeal.catalogue.dto.CreateCatalogueItemRequest;
import com.ordermymeal.catalogue.dto.UpdateCatalogueItemRequest;
import com.ordermymeal.catalogue.service.CatalogueContextService;
import com.ordermymeal.catalogue.service.CreateItemService;
import com.ordermymeal.catalogue.service.GetCatalogueItemService;
import com.ordermymeal.catalogue.service.GetCatalogueItemsService;
import com.ordermymeal.catalogue.service.UpdateItemService;
import com.ordermymeal.catalogue.service.UploadItemPhotoService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/catalogue-items")
@PreAuthorize("hasAuthority('vendor.menu.manage')")
public class CatalogueItemController {

    private final CreateItemService createItemService;
    private final UpdateItemService updateItemService;
    private final UploadItemPhotoService uploadItemPhotoService;
    private final GetCatalogueItemService getCatalogueItemService;
    private final GetCatalogueItemsService getCatalogueItemsService;
    private final CatalogueContextService catalogueContextService;
    private final CurrentSessionService currentSessionService;

    public CatalogueItemController(
            CreateItemService createItemService,
            UpdateItemService updateItemService,
            UploadItemPhotoService uploadItemPhotoService,
            GetCatalogueItemService getCatalogueItemService,
            GetCatalogueItemsService getCatalogueItemsService,
            CatalogueContextService catalogueContextService,
            CurrentSessionService currentSessionService) {

        this.createItemService = createItemService;
        this.updateItemService = updateItemService;
        this.uploadItemPhotoService = uploadItemPhotoService;
        this.getCatalogueItemService = getCatalogueItemService;
        this.getCatalogueItemsService = getCatalogueItemsService;
        this.catalogueContextService = catalogueContextService;
        this.currentSessionService = currentSessionService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('vendor.menu.manage')")
    public ResponseEntity<CatalogueItemResponse> createItem(
            @Valid @RequestBody CreateCatalogueItemRequest request,
            HttpServletRequest httpRequest) {

        Long membershipId = currentSessionService.getCurrentMembershipId(httpRequest);

        CatalogueItemResponse response = createItemService.createItem(
                membershipId,
                request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{catalogueItemId}")
    @PreAuthorize("hasAuthority('vendor.menu.manage')")
    public ResponseEntity<CatalogueItemResponse> getCatalogueItem(
            @PathVariable Long catalogueItemId,
            HttpServletRequest httpRequest) {

        Long membershipId = currentSessionService.getCurrentMembershipId(httpRequest);

        Long organizationId = catalogueContextService.requireOrganization(
                membershipId,
                "vendor.menu.manage");

        CatalogueItemResponse response = getCatalogueItemService.execute(
                catalogueItemId,
                organizationId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('vendor.menu.manage')")
    public ResponseEntity<List<CatalogueItemResponse>> getCatalogueItems(
            @RequestParam(required = false) Long vendorId,
            HttpServletRequest httpRequest) {

        Long membershipId = currentSessionService.getCurrentMembershipId(httpRequest);

        Long organizationId = catalogueContextService.requireOrganization(
                membershipId,
                "vendor.menu.manage");

        List<CatalogueItemResponse> response = getCatalogueItemsService.execute(
                organizationId,
                vendorId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{catalogueItemId}")
    @PreAuthorize("hasAuthority('vendor.menu.manage')")
    public ResponseEntity<CatalogueItemResponse> updateItem(
            @PathVariable Long catalogueItemId,
            @Valid @RequestBody UpdateCatalogueItemRequest request,
            HttpServletRequest httpRequest) {

        Long membershipId = currentSessionService.getCurrentMembershipId(httpRequest);

        CatalogueItemResponse response = updateItemService.updateItem(
                membershipId,
                catalogueItemId,
                request);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{catalogueItemId}/photo")
    @PreAuthorize("hasAuthority('vendor.menu.manage')")
    public ResponseEntity<CatalogueItemResponse> uploadItemPhoto(
            @PathVariable Long catalogueItemId,
            @RequestPart("file") MultipartFile file,
            HttpServletRequest httpRequest) {

        Long membershipId = currentSessionService.getCurrentMembershipId(httpRequest);

        CatalogueItemResponse response = uploadItemPhotoService.uploadItemPhoto(
                membershipId,
                catalogueItemId,
                file);

        return ResponseEntity.ok(response);
    }
}
