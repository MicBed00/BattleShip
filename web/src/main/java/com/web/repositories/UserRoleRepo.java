package com.web.repositories;

import com.web.enity.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepo extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByName(String name);
}
