package com.coffee.app.repository;

import com.coffee.app.domain.Admin;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
   Optional<Admin> findByUserId(Long userId);

   boolean existsByUserId(Long userId);
}
