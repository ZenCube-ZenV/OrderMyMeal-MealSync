package com.ordermymeal.membership.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ordermymeal.membership.dto.MemberCreateRequest;
import com.ordermymeal.membership.dto.MemberResponse;
import com.ordermymeal.membership.dto.MemberUpdateRequest;
import com.ordermymeal.membership.dto.RoleAssignmentRequest;
import com.ordermymeal.membership.dto.RoleResponse;
import com.ordermymeal.membership.service.MembershipService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/members")
public class MembershipController {

    private final MembershipService membershipService;

    public MembershipController(
            MembershipService membershipService) {

        this.membershipService = membershipService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('member.create')")
    public ResponseEntity<MemberResponse> createMember(
            @Valid @RequestBody MemberCreateRequest request,
            HttpServletRequest httpRequest) {

        MemberResponse response = membershipService.createMember(
                request,
                httpRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('member.view')")
    public ResponseEntity<List<MemberResponse>> getMembers(
            HttpServletRequest httpRequest) {

        return ResponseEntity.ok(
                membershipService.getMembers(httpRequest));
    }

    @GetMapping("/{membershipId}")
    @PreAuthorize("hasAuthority('member.view')")
    public ResponseEntity<MemberResponse> getMember(
            @PathVariable Long membershipId,
            HttpServletRequest httpRequest) {

        return ResponseEntity.ok(
                membershipService.getMember(
                        membershipId,
                        httpRequest));
    }

    @PutMapping("/{membershipId}")
    @PreAuthorize("hasAuthority('member.update')")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable Long membershipId,
            @Valid @RequestBody MemberUpdateRequest request,
            HttpServletRequest httpRequest) {

        return ResponseEntity.ok(
                membershipService.updateMember(
                        membershipId,
                        request,
                        httpRequest));
    }

    @DeleteMapping("/{membershipId}")
    @PreAuthorize("hasAuthority('member.deactivate')")
    public ResponseEntity<MemberResponse> deactivateMember(
            @PathVariable Long membershipId,
            HttpServletRequest httpRequest) {

        return ResponseEntity.ok(
                membershipService.deactivateMember(
                        membershipId,
                        httpRequest));
    }

    @PostMapping("/{membershipId}/roles")
    @PreAuthorize("hasAuthority('role.assign')")
    public ResponseEntity<MemberResponse> assignRole(
            @PathVariable Long membershipId,
            @Valid @RequestBody RoleAssignmentRequest request,
            HttpServletRequest httpRequest) {

        return ResponseEntity.ok(
                membershipService.assignRole(
                        membershipId,
                        request.role(),
                        httpRequest));
    }

    @DeleteMapping("/{membershipId}/roles/{roleName}")
    @PreAuthorize("hasAuthority('role.assign')")
    public ResponseEntity<MemberResponse> removeRole(
            @PathVariable Long membershipId,
            @PathVariable String roleName,
            HttpServletRequest httpRequest) {

        return ResponseEntity.ok(
                membershipService.removeRole(
                        membershipId,
                        roleName,
                        httpRequest));
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('role.view')")
    public ResponseEntity<List<RoleResponse>> getAvailableRoles(
            HttpServletRequest httpRequest) {

        return ResponseEntity.ok(
                membershipService.getAvailableRoles(
                        httpRequest));
    }
}