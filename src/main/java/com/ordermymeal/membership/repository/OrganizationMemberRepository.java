package com.ordermymeal.membership.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ordermymeal.membership.model.OrganizationMember;

public interface OrganizationMemberRepository
        extends JpaRepository<OrganizationMember, Long> {

    List<OrganizationMember> findByUserUserId(Long userId);

    List<OrganizationMember> findByOrganizationOrgIdOrderByMembershipIdAsc(
            Long organizationId);

    Optional<OrganizationMember> findByUserUserIdAndOrganizationOrgId(
            Long userId,
            Long organizationId);

    List<OrganizationMember> findByUserUserIdAndStatusOrderByMembershipIdAsc(
            Long userId,
            String status);

    Optional<OrganizationMember> findByUserUserIdAndStatus(
            Long userId,
            String status);
}  