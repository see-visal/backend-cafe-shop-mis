package com.coffee.app.service.impl;

import com.coffee.app.domain.Order;
import com.coffee.app.domain.OrderRating;
import com.coffee.app.domain.Product;
import com.coffee.app.dto.request.RatingRequest;
import com.coffee.app.dto.response.RatingResponse;
import com.coffee.app.exception.ResourceNotFoundException;
import com.coffee.app.repository.OrderRatingRepository;
import com.coffee.app.repository.OrderRepository;
import com.coffee.app.repository.ProductRepository;
import com.coffee.app.service.RatingService;
import java.util.List;
import java.util.UUID;
import lombok.Generated;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RatingServiceImpl implements RatingService {
   private final OrderRatingRepository ratingRepository;
   private final OrderRepository orderRepository;
   private final ProductRepository productRepository;

   @Transactional
   public RatingResponse submitRating(UUID orderId, UUID userId, RatingRequest request) {
      Order order = (Order)this.orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found: " + String.valueOf(orderId)));
      if (!"SERVED".equals(order.getStatus())) {
         throw new IllegalArgumentException("Ratings can only be submitted for SERVED orders.");
      } else if (this.ratingRepository.existsByOrderIdAndUserId(orderId, userId)) {
         throw new IllegalArgumentException("You have already rated this order.");
      } else {
         Product product = (Product)this.productRepository.findById(request.productId()).orElseThrow(() -> new ResourceNotFoundException("Product not found: " + String.valueOf(request.productId())));
         OrderRating rating = OrderRating.builder().order(order).product(product).userId(userId).stars(request.stars()).comment(request.comment()).build();
         OrderRating saved = (OrderRating)this.ratingRepository.save(rating);
         return this.toResponse(saved);
      }
   }

   public List<RatingResponse> getRatingsByProduct(UUID productId) {
      return this.ratingRepository.findByProductIdOrderByCreatedAtDesc(productId).stream().map(this::toResponse).toList();
   }

   public Double getAverageRating(UUID productId) {
      return this.ratingRepository.findAverageStarsByProductId(productId);
   }

   private RatingResponse toResponse(OrderRating r) {
      return new RatingResponse(r.getId(), r.getOrder().getId(), r.getProduct().getId(), r.getProduct().getName(), r.getStars(), r.getComment(), r.getCreatedAt());
   }

   @Generated
   public RatingServiceImpl(final OrderRatingRepository ratingRepository, final OrderRepository orderRepository, final ProductRepository productRepository) {
      this.ratingRepository = ratingRepository;
      this.orderRepository = orderRepository;
      this.productRepository = productRepository;
   }
}
