package com.coffee.app.mapper;

import com.coffee.app.domain.Ingredient;
import com.coffee.app.dto.request.IngredientRequest;
import com.coffee.app.dto.response.IngredientResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
   componentModel = "spring"
)
public interface IngredientMapper {
   @Mapping(
      target = "lowStock",
      expression = "java(ingredient.getStockQty().compareTo(ingredient.getLowThreshold()) <= 0)"
   )
   IngredientResponse toResponse(Ingredient ingredient);

   @Mapping(
      target = "id",
      ignore = true
   )
   Ingredient toEntity(IngredientRequest request);

   @Mapping(
      target = "id",
      ignore = true
   )
   void updateEntity(IngredientRequest request, @MappingTarget Ingredient ingredient);
}
