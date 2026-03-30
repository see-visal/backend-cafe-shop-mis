package com.coffee.app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffee.app.domain.Category;
import com.coffee.app.dto.request.CategoryRequest;
import com.coffee.app.dto.response.CategoryResponse;
import com.coffee.app.exception.ResourceNotFoundException;
import com.coffee.app.mapper.CategoryMapper;
import com.coffee.app.repository.CategoryRepository;
import com.coffee.app.repository.ProductRepository;
import com.coffee.app.service.CategoryService;

import lombok.Generated;

@Service
public class CategoryServiceImpl implements CategoryService {
   private final CategoryRepository categoryRepository;
   private final ProductRepository productRepository;
   private final CategoryMapper categoryMapper;

   public List<CategoryResponse> getAll() {
      return this.categoryRepository.findAll().stream().map(this.categoryMapper::toResponse).toList();
   }

   @Transactional
   public CategoryResponse create(CategoryRequest request) {
      if (this.categoryRepository.existsByName(request.name())) {
         throw new IllegalArgumentException("Category already exists: " + request.name());
      } else {
         return this.categoryMapper.toResponse(this.categoryRepository.save(this.categoryMapper.toEntity(request)));
      }
   }

   @Transactional
   public CategoryResponse update(Integer id, CategoryRequest request) {
      Category category = this.categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
      String existingImageUrl = category.getImageUrl();
      this.categoryMapper.updateEntity(request, category);
      if (request.imageUrl() == null) {
         category.setImageUrl(existingImageUrl);
      }
      return this.categoryMapper.toResponse(this.categoryRepository.save(category));
   }

   @Transactional
   public CategoryResponse updateCategoryImage(Integer id, String imageUrl) {
      Category category = this.categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
      category.setImageUrl(imageUrl);
      return this.categoryMapper.toResponse(this.categoryRepository.save(category));
   }

   @Transactional
   public void delete(Integer id) {
      if (!this.categoryRepository.existsById(id)) {
         throw new ResourceNotFoundException("Category not found: " + id);
      } else if (this.productRepository.existsByCategoryId(id)) {
         throw new IllegalArgumentException("Cannot delete category because it is used by existing products.");
      } else {
         this.categoryRepository.deleteById(id);
      }
   }

   @Generated
   public CategoryServiceImpl(final CategoryRepository categoryRepository, final ProductRepository productRepository, final CategoryMapper categoryMapper) {
      this.categoryRepository = categoryRepository;
      this.productRepository = productRepository;
      this.categoryMapper = categoryMapper;
   }
}
