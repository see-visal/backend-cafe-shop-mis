package com.coffee.app.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ReceiptResponse(UUID orderId, UUID paymentId, String orderType, Integer tableNumber, List<ReceiptItemResponse> items, BigDecimal subtotal, BigDecimal totalPaid, String paymentMethod, LocalDateTime paidAt) {
   public static record ReceiptItemResponse(String productName, int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {
   }
}
