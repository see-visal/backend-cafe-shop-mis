package com.coffee.app.service;

import com.coffee.app.dto.request.RatingRequest;
import com.coffee.app.dto.response.RatingResponse;
import java.util.List;
import java.util.UUID;

public interface RatingService {
   RatingResponse submitRating(UUID orderId, UUID userId, RatingRequest request);

   List<RatingResponse> getRatingsByProduct(UUID productId);

   Double getAverageRating(UUID productId);
}
