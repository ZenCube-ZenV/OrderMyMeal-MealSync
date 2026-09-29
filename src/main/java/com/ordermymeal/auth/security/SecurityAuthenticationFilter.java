package com.ordermymeal.auth.security;

import com.ordermymeal.auth.model.AuthSession;
import com.ordermymeal.auth.model.Permission;
import com.ordermymeal.auth.repository.SessionRepository;
import com.ordermymeal.auth.service.AuthorizationService;
import com.ordermymeal.auth.service.SessionTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SecurityAuthenticationFilter extends OncePerRequestFilter {

    private static final String SESSION_COOKIE = "OMM_SESSION";

    private final SessionRepository sessionRepository;
    private final SessionTokenService sessionTokenService;
    private final AuthorizationService authorizationService;

    public SecurityAuthenticationFilter(
            SessionRepository sessionRepository,
            SessionTokenService sessionTokenService,
            AuthorizationService authorizationService) {

        this.sessionRepository = sessionRepository;
        this.sessionTokenService = sessionTokenService;
        this.authorizationService = authorizationService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = extractSessionToken(request);

        if (token != null && !token.isBlank()) {
            try {
                byte[] tokenHash = sessionTokenService.hashToken(token);

                AuthSession session = sessionRepository
                        .findByTokenHashAndRevokedAtIsNullAndExpiresAtAfter(tokenHash, Instant.now())
                        .orElse(null);

                if (session != null) {
                    Set<Permission> permissions = new HashSet<>();

                    if (session.getMembershipId() != null) {
                        permissions.addAll(authorizationService.getPermissions(session.getMembershipId()));
                    }

                    if (session.getUserId() != null) {
                        permissions.addAll(authorizationService.getUserPermissions(session.getUserId()));
                    }

                    Set<GrantedAuthority> authorities = permissions.stream()
                            .map(Permission::getName)
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toSet());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(session, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception ex) {
                SecurityContextHolder.clearContext();
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Keep security context for request thread duration
        }
    }

    private String extractSessionToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> SESSION_COOKIE.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}