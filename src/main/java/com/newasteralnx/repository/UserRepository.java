package com.newasteralnx.repository;

import com.newasteralnx.constant.Role;
import com.newasteralnx.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    Page<User> findByDeletedFalse(Pageable pageable);

    Page<User> findByDeletedFalseAndRole(Role role, Pageable pageable);

    boolean existsByRoleAndDeletedFalse(Role role);

    long countByRoleAndDeletedFalseAndActiveTrue(Role role);
}
