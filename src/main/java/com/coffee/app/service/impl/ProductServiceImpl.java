package com.coffee.app.service.impl;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffee.app.domain.Category;
import com.coffee.app.domain.Product;
import com.coffee.app.dto.request.ProductRequest;
import com.coffee.app.dto.response.HomeShowcaseResponse;
import com.coffee.app.dto.response.ProductResponse;
import com.coffee.app.exception.ResourceNotFoundException;
import com.coffee.app.mapper.ProductMapper;
import com.coffee.app.repository.ProductRepository;
import com.coffee.app.service.ProductService;

import jakarta.persistence.EntityManager;
import lombok.Generated;
import org.springframework.data.domain.PageRequest;

@Service
public class ProductServiceImpl implements ProductService {
   private final ProductRepository productRepository;
   private final EntityManager entityManager;
   private final ProductMapper productMapper;

   public Page<ProductResponse> getAllProducts(Pageable pageable) {
      return this.productRepository.findAll(pageable).map(this.productMapper::toResponse);
   }

   public Page<ProductResponse> getActiveProducts(Pageable pageable) {
      return this.productRepository.findByActiveTrue(pageable).map(this.productMapper::toResponse);
   }

   public Page<ProductResponse> getActiveProductsByCategory(Integer categoryId, Pageable pageable) {
      return this.productRepository.findByActiveTrueAndCategoryId(categoryId, pageable).map(this.productMapper::toResponse);
   }

   public Page<ProductResponse> searchActiveProducts(String search, Integer categoryId, Pageable pageable) {
      return this.productRepository.searchActiveProducts(search != null && !search.isBlank() ? search.trim() : null, categoryId, pageable).map(this.productMapper::toResponse);
   }

   public ProductResponse getProductById(UUID id) {
      return this.productMapper.toResponse(this.findOrThrow(id));
   }

   @Transactional
   public ProductResponse createProduct(ProductRequest request) {
      Product product = this.productMapper.toEntity(request);
      if (request.categoryId() != null) {
         product.setCategory(this.entityManager.getReference(Category.class, request.categoryId()));
      }

      return this.productMapper.toResponse(this.productRepository.save(product));
   }

   @Transactional
   public ProductResponse updateProduct(UUID id, ProductRequest request) {
      Product product = this.findOrThrow(id);
      this.productMapper.updateEntity(request, product);
      if (request.categoryId() != null) {
         product.setCategory(this.entityManager.getReference(Category.class, request.categoryId()));
      }

      return this.productMapper.toResponse(this.productRepository.save(product));
   }

   @Transactional
   public ProductResponse updateProductImage(UUID id, String imageUrl) {
      Product product = this.findOrThrow(id);
      product.setImageUrl(imageUrl);
      return this.productMapper.toResponse(this.productRepository.save(product));
   }

   public HomeShowcaseResponse getHomeShowcase() {
      var specialProducts = this.productRepository.findActiveTodaySpecials(Pageable.unpaged()).stream()
         .map(this.productMapper::toResponse)
         .toList();
      var featuredProducts = this.productRepository.findActiveHomepageProducts(PageRequest.of(0, 8)).stream()
         .filter((product) -> !product.isTodaySpecial())
         .map(this.productMapper::toResponse)
         .toList();
      return new HomeShowcaseResponse(specialProducts, featuredProducts);
   }

   @Transactional
   public void softDelete(UUID id) {
      Product product = this.findOrThrow(id);
      product.setActive(false);
      this.productRepository.save(product);
   }

   @Transactional
   public ProductResponse setAvailability(UUID id, boolean available) {
      Product product = this.findOrThrow(id);
      product.setActive(available);
      return this.productMapper.toResponse(this.productRepository.save(product));
   }

   private Product findOrThrow(UUID id) {
      return this.productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found: " + String.valueOf(id)));
   }

   @Generated
   public ProductServiceImpl(final ProductRepository productRepository, final EntityManager entityManager, final ProductMapper productMapper) {
      this.productRepository = productRepository;
      this.entityManager = entityManager;
      this.productMapper = productMapper;
   }
}
