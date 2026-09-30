package com.ordermymeal.auth.service;

import com.ordermymeal.auth.config.AuthProperties;
import com.ordermymeal.auth.model.AuthPurpose;
import com.ordermymeal.auth.model.OneTimeCode;
import com.ordermymeal.auth.model.Role;
import com.ordermymeal.auth.model.User;
import com.ordermymeal.auth.model.UserRole;
import com.ordermymeal.auth.repository.OneTimeCodeRepository;
import com.ordermymeal.auth.repository.UserRepository;
import com.ordermymeal.auth.repository.UserRoleRepository;
import com.ordermymeal.membership.model.OrganizationMember;
import com.ordermymeal.membership.repository.OrganizationMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class VerifyOtpService {

    private final OneTimeCodeRepository oneTimeCodeRepository;
    private final UserRepository userRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final OtpHashService otpHashService;
    private final AuthenticationSessionService sessionService;
    private final AuthProperties authProperties;
    private final UserRoleRepository userRoleRepository;

    public VerifyOtpService(
            OneTimeCodeRepository oneTimeCodeRepository,
            UserRepository userRepository,
            OrganizationMemberRepository organizationMemberRepository,
            OtpHashService otpHashService,
            AuthenticationSessionService sessionService,
            AuthProperties authProperties,
            UserRoleRepository userRoleRepository) {

        this.oneTimeCodeRepository = oneTimeCodeRepository;
        this.userRepository = userRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.otpHashService = otpHashService;
        this.sessionService = sessionService;
        this.authProperties = authProperties;
        this.userRoleRepository = userRoleRepository;
    }

    @Transactional
    public AuthenticationSessionService.SessionResult verifyOtp(
            String email,
            String otp) {

        String normalizedEmail = email.trim().toLowerCase();
        Instant now = Instant.now();

        OneTimeCode oneTimeCode =
                oneTimeCodeRepository
                        .findTopByEmailAndPurposeAndConsumedAtIsNullAndExpiresAtAfterOrderByCreatedAtDesc(
                                normalizedEmail,
                                AuthPurpose.SIGN_IN,
                                now)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid or expired OTP."
                                ));

        if (oneTimeCode.getAttempts()
                >= authProperties.getMaxOtpAttempts()) {

            throw new IllegalArgumentException(
                    "Maximum OTP attempts exceeded."
            );
        }

        oneTimeCode.incrementAttempts();

        byte[] expectedHash =
                otpHashService.hash(
                        normalizedEmail,
                        otp);

        if (!constantTimeEquals(
                expectedHash,
                oneTimeCode.getCodeHash())) {

            oneTimeCodeRepository.save(oneTimeCode);

            throw new IllegalArgumentException(
                    "Invalid or expired OTP."
            );
        }

        User user =
                userRepository.findByEmail(normalizedEmail)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid or expired OTP."
                                ));

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new IllegalArgumentException(
                    "Account is not active."
            );
        }

        List<UserRole> userRoles =
                userRoleRepository
                        .findByUserUserId(user.getUserId());

        boolean isSuperadmin =
                userRoles.stream()
                        .map(UserRole::getRole)
                        .anyMatch(role ->
                                "superadmin".equalsIgnoreCase(
                                        role.getName()));

        if (isSuperadmin) {

            oneTimeCode.consume();
            oneTimeCodeRepository.save(oneTimeCode);

            List<Role> roles =
                    userRoles.stream()
                            .map(UserRole::getRole)
                            .toList();

            return sessionService.createSuperadminSession(
                    user,
                    roles);
        }

        List<OrganizationMember> memberships =
                organizationMemberRepository
                        .findByUserUserIdAndStatusOrderByMembershipIdAsc(
                                user.getUserId(),
                                "ACTIVE");

        OrganizationMember membership =
                selectMembership(memberships);

        oneTimeCode.consume();
        oneTimeCodeRepository.save(oneTimeCode);

        return sessionService.createSession(
                membership);
    }

    private OrganizationMember selectMembership(
            List<OrganizationMember> memberships) {

        if (memberships.isEmpty()) {
            throw new IllegalArgumentException(
                    "No active organization membership found."
            );
        }

        if (memberships.size() > 1) {
            throw new IllegalArgumentException(
                    "Multiple active organization memberships found. "
                            + "Organization selection is required."
            );
        }

        return memberships.get(0);
    }

    private boolean constantTimeEquals(
            byte[] first,
            byte[] second) {

        if (first == null
                || second == null
                || first.length != second.length) {

            return false;
        }

        int result = 0;

        for (int i = 0; i < first.length; i++) {
            result |= first[i] ^ second[i];
        }

        return result == 0;
    }
}
