package com.coffee.app.mapper;

import com.coffee.app.domain.Ingredient;
import com.coffee.app.dto.request.IngredientRequest;
import com.coffee.app.dto.response.IngredientResponse;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class IngredientMapperImpl implements IngredientMapper {
   public IngredientResponse toResponse(Ingredient ingredient) {
      if (ingredient == null) {
         return null;
      } else {
         UUID id = null;
         String name = null;
         String unit = null;
         BigDecimal stockQty = null;
         BigDecimal lowThreshold = null;
         String supplier = null;
         String imageUrl = null;
         id = ingredient.getId();
         name = ingredient.getName();
         unit = ingredient.getUnit();
         stockQty = ingredient.getStockQty();
         lowThreshold = ingredient.getLowThreshold();
         supplier = ingredient.getSupplier();
         imageUrl = ingredient.getImageUrl();
         boolean lowStock = ingredient.getStockQty().compareTo(ingredient.getLowThreshold()) <= 0;
         IngredientResponse ingredientResponse = new IngredientResponse(id, name, unit, stockQty, lowThreshold, supplier, imageUrl, lowStock);
         return ingredientResponse;
      }
   }

   public Ingredient toEntity(IngredientRequest request) {
      if (request == null) {
         return null;
      } else {
         Ingredient.IngredientBuilder ingredient = Ingredient.builder();
         ingredient.name(request.name());
         ingredient.unit(request.unit());
         ingredient.stockQty(request.stockQty());
         ingredient.lowThreshold(request.lowThreshold());
         ingredient.supplier(request.supplier());
         ingredient.imageUrl(request.imageUrl());
         return ingredient.build();
      }
   }

   public void updateEntity(IngredientRequest request, Ingredient ingredient) {
      if (request != null) {
         ingredient.setName(request.name());
         ingredient.setUnit(request.unit());
         ingredient.setStockQty(request.stockQty());
         ingredient.setLowThreshold(request.lowThreshold());
         ingredient.setSupplier(request.supplier());
         ingredient.setImageUrl(request.imageUrl());
      }
   }
}
