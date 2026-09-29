package com.ordermymeal.auth.controller;

import com.ordermymeal.auth.service.AuthorizationService;
import com.ordermymeal.auth.service.CurrentSessionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/test")
public class AuthorizationTestController {

        private final AuthorizationService authorizationService;
        private final CurrentSessionService currentSessionService;

        public AuthorizationTestController(
                        AuthorizationService authorizationService,
                        CurrentSessionService currentSessionService) {
                this.authorizationService = authorizationService;
                this.currentSessionService = currentSessionService;
        }

        @GetMapping("/user")
        public ResponseEntity<String> userAccess(
                        HttpServletRequest request) {

                Long membershipId = currentSessionService.getCurrentMembershipId(request);

                authorizationService.requirePermission(
                                membershipId,
                                "organization.view");

                return ResponseEntity.ok(
                                "Access granted: organization.view");
        }

        @GetMapping("/admin")
        public ResponseEntity<String> adminAccess(
                        HttpServletRequest request) {

                Long membershipId = currentSessionService.getCurrentMembershipId(request);

                authorizationService.requirePermission(
                                membershipId,
                                "member.create");

                return ResponseEntity.ok(
                                "Access granted: member.create");
        }
}

