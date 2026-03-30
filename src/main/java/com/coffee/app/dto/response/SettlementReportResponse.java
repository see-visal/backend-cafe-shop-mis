package com.coffee.app.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

public record SettlementReportResponse(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate date,
        long totalOrders,
        BigDecimal totalRevenue,
        BigDecimal cashRevenue,
        long cashOrders,
        BigDecimal khqrRevenue,
        long khqrOrders,
        BigDecimal cardRevenue,
        long cardOrders,
        BigDecimal totalRefunds,
        BigDecimal netRevenue
) {
}
