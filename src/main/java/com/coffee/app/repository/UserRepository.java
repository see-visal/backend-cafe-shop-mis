package com.coffee.app.repository;

import com.coffee.app.domain.User;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository extends JpaRepository<User, Long> {
   Optional<User> findByUsername(String username);

   Optional<User> findByEmail(String email);

   Optional<User> findByUuid(String uuid);

   boolean existsByEmail(String email);

   boolean existsByUsername(String username);

   Page<User> findDistinctByRoles_NameIn(Collection<String> roleNames, Pageable pageable);

   @Query("SELECT SUM(u.loyaltyPoints) FROM User u WHERE u.loyaltyPoints > 0")
   Long sumLoyaltyPoints();

   @Query("SELECT COUNT(u) FROM User u WHERE u.loyaltyPoints > 0")
   Long countUsersWithPoints();
}
