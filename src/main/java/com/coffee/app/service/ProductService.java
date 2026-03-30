package com.coffee.app.service;

import com.coffee.app.dto.request.ProductRequest;
import com.coffee.app.dto.response.HomeShowcaseResponse;
import com.coffee.app.dto.response.ProductResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
   Page<ProductResponse> getAllProducts(Pageable pageable);

   Page<ProductResponse> getActiveProducts(Pageable pageable);

   Page<ProductResponse> getActiveProductsByCategory(Integer categoryId, Pageable pageable);

   Page<ProductResponse> searchActiveProducts(String search, Integer categoryId, Pageable pageable);

   ProductResponse getProductById(UUID id);

   ProductResponse createProduct(ProductRequest request);

   ProductResponse updateProduct(UUID id, ProductRequest request);

   ProductResponse updateProductImage(UUID id, String imageUrl);

   HomeShowcaseResponse getHomeShowcase();

   void softDelete(UUID id);

   ProductResponse setAvailability(UUID id, boolean available);
}
