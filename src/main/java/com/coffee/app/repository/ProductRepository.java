package com.coffee.app.repository;

import com.coffee.app.domain.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, UUID> {
   Optional<Product> findFirstByName(String name);

   @EntityGraph(
      attributePaths = {"category"}
   )
   Page<Product> findAll(Pageable pageable);

   @EntityGraph(
      attributePaths = {"category"}
   )
   Page<Product> findByActiveTrue(Pageable pageable);

   @EntityGraph(
      attributePaths = {"category"}
   )
   Page<Product> findByActiveTrueAndCategoryId(Integer categoryId, Pageable pageable);

   @EntityGraph(
      attributePaths = {"category"}
   )
   @Query("SELECT p FROM Product p WHERE p.active = true AND (CAST(:categoryId AS integer) IS NULL OR p.category.id = :categoryId) AND (CAST(:search AS string) IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))      OR LOWER(p.description) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))")
   Page<Product> searchActiveProducts(@Param("search") String search, @Param("categoryId") Integer categoryId, Pageable pageable);

   @EntityGraph(
      attributePaths = {"category"}
   )
   @Query("SELECT p FROM Product p WHERE p.active = true AND p.todaySpecial = true ORDER BY CASE WHEN p.homePriority IS NULL THEN 1 ELSE 0 END, p.homePriority ASC, p.createdAt DESC")
   List<Product> findActiveTodaySpecials(Pageable pageable);

   @EntityGraph(
      attributePaths = {"category"}
   )
   @Query("SELECT p FROM Product p WHERE p.active = true AND p.showOnHomepage = true ORDER BY CASE WHEN p.homePriority IS NULL THEN 1 ELSE 0 END, p.homePriority ASC, p.createdAt DESC")
   List<Product> findActiveHomepageProducts(Pageable pageable);

   boolean existsByCategoryId(Integer categoryId);
}
