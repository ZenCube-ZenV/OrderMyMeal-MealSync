package com.ordermymeal.auth.repository;

import com.ordermymeal.auth.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findByUserUserId(Long userId);

    @Query("""
            SELECT ur
            FROM UserRole ur
            JOIN FETCH ur.role
            WHERE ur.user.userId = :userId
            """)
    List<UserRole> findWithRolesByUserId(
            @Param("userId") Long userId);
}
