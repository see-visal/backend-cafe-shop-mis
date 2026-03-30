package com.coffee.app.service;

import com.coffee.app.dto.request.CategoryRequest;
import com.coffee.app.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {
   List<CategoryResponse> getAll();

   CategoryResponse create(CategoryRequest request);

   CategoryResponse update(Integer id, CategoryRequest request);

   CategoryResponse updateCategoryImage(Integer id, String imageUrl);

   void delete(Integer id);
}
