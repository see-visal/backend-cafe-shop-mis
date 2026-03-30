package com.coffee.app.service;

import com.coffee.app.dto.request.IngredientRequest;
import com.coffee.app.dto.request.StockAdjustRequest;
import com.coffee.app.dto.response.IngredientResponse;
import java.util.List;
import java.util.UUID;

public interface InventoryService {
   List<IngredientResponse> getAllIngredients();

   IngredientResponse getIngredientById(UUID id);

   List<IngredientResponse> getLowStockIngredients();

   IngredientResponse createIngredient(IngredientRequest request);

   IngredientResponse updateIngredient(UUID id, IngredientRequest request);

   IngredientResponse updateIngredientImage(UUID id, String imageUrl);

   IngredientResponse adjustStock(StockAdjustRequest request);

   void deleteIngredient(UUID id);
}
