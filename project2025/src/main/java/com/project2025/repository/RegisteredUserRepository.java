package com.project2025.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project2025.enums.Role;
import com.project2025.enums.UserStatus;
import com.project2025.model.RegisteredUser;

@Repository
public interface RegisteredUserRepository extends JpaRepository<RegisteredUser, Long> {

    Optional<RegisteredUser> findByMail(String mail);

    boolean existsByMail(String mail);

    List<RegisteredUser> findByRole(Role role);

    Page<RegisteredUser> findByRole(Role role, Pageable pageable);

    List<RegisteredUser> findByStatus(UserStatus status);

    Page<RegisteredUser> findByStatus(UserStatus status, Pageable pageable);

    List<RegisteredUser> findByIsBlocked(Boolean isBlocked);

    Page<RegisteredUser> findByIsBlocked(Boolean isBlocked, Pageable pageable);

    Page<RegisteredUser> findByRoleAndStatus(Role role, UserStatus status, Pageable pageable);

    Page<RegisteredUser> findByRoleAndIsBlocked(Role role, Boolean isBlocked, Pageable pageable);

    Page<RegisteredUser> findByRoleAndStatusAndIsBlocked(Role role, UserStatus status, Boolean isBlocked, Pageable pageable);
    
    Optional<RegisteredUser> findByMailAndPassword(String mail, String password);

}
