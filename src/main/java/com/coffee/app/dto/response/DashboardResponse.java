package com.coffee.app.dto.response;

public record DashboardResponse(long totalOrders, long pendingOrders, long preparingOrders, long readyOrders, long totalProducts, long lowStockIngredients) {
}
