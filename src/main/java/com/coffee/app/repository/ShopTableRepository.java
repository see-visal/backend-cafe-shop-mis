package com.coffee.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coffee.app.domain.ShopTable;

public interface ShopTableRepository extends JpaRepository<ShopTable, Long> {
   boolean existsByTableNumber(Integer tableNumber);

   boolean existsByTableNumberAndIdNot(Integer tableNumber, Long id);

   Optional<ShopTable> findByTableNumber(Integer tableNumber);

   List<ShopTable> findAllByOrderByTableNumberAsc();

   List<ShopTable> findByActiveTrueOrderByTableNumberAsc();
}
