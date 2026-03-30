package com.coffee.app.service.impl;

import com.coffee.app.dto.response.DashboardResponse;
import com.coffee.app.repository.IngredientRepository;
import com.coffee.app.repository.OrderRepository;
import com.coffee.app.repository.ProductRepository;
import com.coffee.app.service.AdminService;
import lombok.Generated;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
   private final OrderRepository orderRepository;
   private final ProductRepository productRepository;
   private final IngredientRepository ingredientRepository;

   public DashboardResponse getDashboard() {
      long totalOrders = this.orderRepository.count();
      long pendingOrders = this.orderRepository.countByStatus("CONFIRMED");
      long preparingOrders = this.orderRepository.countByStatus("PREPARING");
      long readyOrders = this.orderRepository.countByStatus("READY");
      long totalProducts = this.productRepository.count();
      long lowStock = this.ingredientRepository.findAll().stream().filter((i) -> i.getStockQty().compareTo(i.getLowThreshold()) <= 0).count();
      return new DashboardResponse(totalOrders, pendingOrders, preparingOrders, readyOrders, totalProducts, lowStock);
   }

   @Generated
   public AdminServiceImpl(final OrderRepository orderRepository, final ProductRepository productRepository, final IngredientRepository ingredientRepository) {
      this.orderRepository = orderRepository;
      this.productRepository = productRepository;
      this.ingredientRepository = ingredientRepository;
   }
}
