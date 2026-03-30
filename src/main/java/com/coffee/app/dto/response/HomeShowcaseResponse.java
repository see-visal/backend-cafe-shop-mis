package com.coffee.app.dto.response;

import java.util.List;

public record HomeShowcaseResponse(List<ProductResponse> todaySpecials, List<ProductResponse> featuredProducts) {
}
