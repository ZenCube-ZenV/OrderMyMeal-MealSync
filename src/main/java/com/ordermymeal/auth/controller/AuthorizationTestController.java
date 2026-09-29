package com.ordermymeal.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/test")
public class AuthorizationTestController {

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('organization.view')")
    public ResponseEntity<String> userAccess() {
        return ResponseEntity.ok(
                "Access granted: organization.view");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('member.create')")
    public ResponseEntity<String> adminAccess() {
        return ResponseEntity.ok(
                "Access granted: member.create");
    }
}
