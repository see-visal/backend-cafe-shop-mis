package com.coffee.app.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffee.app.domain.Ingredient;
import com.coffee.app.dto.request.IngredientRequest;
import com.coffee.app.dto.request.StockAdjustRequest;
import com.coffee.app.dto.response.IngredientResponse;
import com.coffee.app.exception.ResourceNotFoundException;
import com.coffee.app.mapper.IngredientMapper;
import com.coffee.app.repository.IngredientRepository;
import com.coffee.app.service.InventoryService;

import lombok.Generated;

@Service
public class InventoryServiceImpl implements InventoryService {
   private final IngredientRepository ingredientRepository;
   private final IngredientMapper ingredientMapper;

   public List<IngredientResponse> getAllIngredients() {
      return this.ingredientRepository.findAll().stream().map(this.ingredientMapper::toResponse).toList();
   }

   public IngredientResponse getIngredientById(UUID id) {
      return this.ingredientMapper.toResponse(this.findOrThrow(id));
   }

   public List<IngredientResponse> getLowStockIngredients() {
      return this.ingredientRepository.findLowStockIngredients().stream().map(this.ingredientMapper::toResponse).toList();
   }

   @Transactional
   public IngredientResponse createIngredient(IngredientRequest request) {
      return this.ingredientMapper.toResponse(this.ingredientRepository.save(this.ingredientMapper.toEntity(request)));
   }

   @Transactional
   public IngredientResponse updateIngredient(UUID id, IngredientRequest request) {
      Ingredient ingredient = this.findOrThrow(id);
      String existingImageUrl = ingredient.getImageUrl();
      this.ingredientMapper.updateEntity(request, ingredient);
      if (request.imageUrl() == null) {
         ingredient.setImageUrl(existingImageUrl);
      }
      return this.ingredientMapper.toResponse(this.ingredientRepository.save(ingredient));
   }

   @Transactional
   public IngredientResponse updateIngredientImage(UUID id, String imageUrl) {
      Ingredient ingredient = this.findOrThrow(id);
      ingredient.setImageUrl(imageUrl);
      return this.ingredientMapper.toResponse(this.ingredientRepository.save(ingredient));
   }

   @Transactional
   public IngredientResponse adjustStock(StockAdjustRequest request) {
      Ingredient ingredient = this.findOrThrow(request.ingredientId());
      BigDecimal newQty = ingredient.getStockQty().add(request.delta());
      if (newQty.compareTo(BigDecimal.ZERO) < 0) {
         String var10002 = String.valueOf(ingredient.getStockQty());
         throw new IllegalArgumentException("Stock cannot go negative. Current: " + var10002 + ", Delta: " + String.valueOf(request.delta()));
      } else {
         ingredient.setStockQty(newQty);
         return this.ingredientMapper.toResponse(this.ingredientRepository.save(ingredient));
      }
   }

   @Transactional
   public void deleteIngredient(UUID id) {
      this.ingredientRepository.delete(this.findOrThrow(id));
   }

   private Ingredient findOrThrow(UUID id) {
      return this.ingredientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ingredient not found: " + String.valueOf(id)));
   }

   @Generated
   public InventoryServiceImpl(final IngredientRepository ingredientRepository, final IngredientMapper ingredientMapper) {
      this.ingredientRepository = ingredientRepository;
      this.ingredientMapper = ingredientMapper;
   }
}
