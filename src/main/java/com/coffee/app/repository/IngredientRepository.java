package com.coffee.app.repository;

import com.coffee.app.domain.Ingredient;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {
   Optional<Ingredient> findByName(String name);

   @Query("SELECT i FROM Ingredient i WHERE i.stockQty <= i.lowThreshold")
   List<Ingredient> findLowStockIngredients();
}
