package com.ordermymeal.membership.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ordermymeal.membership.model.MembershipRole;

public interface MembershipRoleRepository
        extends JpaRepository<MembershipRole, Long> {

    List<MembershipRole> findByMembershipId(Long membershipId);

    Optional<MembershipRole> findByMembershipIdAndRole_RoleId(
            Long membershipId,
            UUID roleId);

    boolean existsByMembershipIdAndRole_RoleId(
            Long membershipId,
            UUID roleId);
} 