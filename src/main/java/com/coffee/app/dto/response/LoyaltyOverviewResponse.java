package com.coffee.app.dto.response;

public record LoyaltyOverviewResponse(long totalPointsIssued, long totalPointsRedeemed, long activeUsers, long totalUsersWithPoints) {
}
