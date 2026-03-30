package com.coffee.app.mapper;

import com.coffee.app.domain.Category;
import com.coffee.app.dto.request.CategoryRequest;
import com.coffee.app.dto.response.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
   componentModel = "spring"
)
public interface CategoryMapper {
   CategoryResponse toResponse(Category category);

   @Mapping(
      target = "id",
      ignore = true
   )
   Category toEntity(CategoryRequest request);

   @Mapping(
      target = "id",
      ignore = true
   )
   void updateEntity(CategoryRequest request, @MappingTarget Category category);
}
