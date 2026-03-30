package com.coffee.app.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coffee.app.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
   Optional<Payment> findByOrderId(UUID orderId);

   List<Payment> findByOrderIdIn(List<UUID> orderIds);
}
