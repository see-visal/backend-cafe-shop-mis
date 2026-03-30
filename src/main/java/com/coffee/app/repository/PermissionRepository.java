package com.coffee.app.repository;

import com.coffee.app.domain.Permission;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
   Optional<Permission> findByName(String name);
}
