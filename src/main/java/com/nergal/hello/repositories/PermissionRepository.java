package com.nergal.hello.repositories;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nergal.hello.entities.UserPermission;

@Repository
public interface PermissionRepository extends JpaRepository<UserPermission, Long> {
    @Query("""
        select up.name
        from UserPermission up
        where up.user.username = :username
    """)
    List<String> findPermissionNamesByUsername(@Param("username") String username);

    boolean existsByNameAndUser_UserId(String name, UUID userId);
}
