package com.coffee.app.mapper;

import com.coffee.app.domain.Category;
import com.coffee.app.dto.request.CategoryRequest;
import com.coffee.app.dto.response.CategoryResponse;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapperImpl implements CategoryMapper {
   public CategoryResponse toResponse(Category category) {
      if (category == null) {
         return null;
      } else {
         Integer id = null;
         String name = null;
         String icon = null;
         String imageUrl = null;
         id = category.getId();
         name = category.getName();
         icon = category.getIcon();
         imageUrl = category.getImageUrl();
         CategoryResponse categoryResponse = new CategoryResponse(id, name, icon, imageUrl);
         return categoryResponse;
      }
   }

   public Category toEntity(CategoryRequest request) {
      if (request == null) {
         return null;
      } else {
         Category category = new Category();
         category.setName(request.name());
         category.setIcon(request.icon());
         category.setImageUrl(request.imageUrl());
         return category;
      }
   }

   public void updateEntity(CategoryRequest request, Category category) {
      if (request != null) {
         category.setName(request.name());
         category.setIcon(request.icon());
         category.setImageUrl(request.imageUrl());
      }
   }
}
